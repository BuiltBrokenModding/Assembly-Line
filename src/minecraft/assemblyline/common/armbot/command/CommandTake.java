package assemblyline.common.armbot.command;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import dark.library.machine.crafting.AutoCraftingManager;

public class CommandTake extends Command
{
	private ItemStack stack;

	@Override
	public void onTaskStart()
	{
		int id = 0;
		int meta = 32767;
		int count = 1;

		if (this.getArgs().length > 0)
		{
			String block = this.getArg(0);
			if (block.contains(":"))
			{
				String[] blockID = block.split(":");
				id = Integer.parseInt(blockID[0]);
				meta = Integer.parseInt(blockID[1]);
			}
			else
			{
				id = Integer.parseInt(block);
			}
		}
		if (this.getArgs().length > 1)
		{
			count = this.getIntArg(1);
		}
		if (id == 0)
		{
			stack = null;
		}
		else
		{
			stack = new ItemStack(id, count, meta);
		}
	}

	@Override
	protected boolean doTask()
	{
		TileEntity targetTile = this.tileEntity.getHandPosition().getTileEntity(this.world);
		ForgeDirection direction = this.tileEntity.getFacingDirectionFromAngle();
		if (targetTile != null && this.tileEntity.getGrabbedItems().size() <= 0)
		{
			if (targetTile instanceof ISidedInventory)
			{
				ISidedInventory inventory = (ISidedInventory) targetTile;
				int[] slots = inventory.getAccessibleSlotsFromSide(direction.getOpposite().ordinal());
				for (int i = 0; i < slots.length; i++)
				{
					ItemStack slotStack = inventory.getStackInSlot(slots[i]);
					if (this.stack != null)
					{
						if (AutoCraftingManager.areStacksEqual(this.stack, slotStack) && inventory.canExtractItem(slots[i], this.stack, direction.getOpposite().ordinal()))
						{
							ItemStack insertStack = this.stack.copy();
							insertStack.stackSize = Math.min(this.stack.stackSize, slotStack.stackSize);
							this.tileEntity.grabItem(insertStack);
							inventory.setInventorySlotContents(slots[i], AutoCraftingManager.decrStackSize(slotStack, insertStack.stackSize));
							return false;
						}
					}
				}
			}// TODO add a way to steal items from players

		}
		return true;
	}

	@Override
	public String toString()
	{
		return "Take " + (stack != null ? stack.toString() : "1x???@???  ");
	}

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.stack = ItemStack.loadItemStackFromNBT(taskCompound.getCompoundTag("item"));
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		if (stack != null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			this.stack.writeToNBT(tag);
			taskCompound.setTag("item", tag);
		}
	}
}
