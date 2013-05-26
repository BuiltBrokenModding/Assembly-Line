package assemblyline.common.armbot.command;

import java.util.Iterator;

import universalelectricity.core.vector.Vector3;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import dark.library.machine.crafting.AutoCraftingManager;

public class CommandGive extends Command
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
		if (targetTile != null && this.tileEntity.getGrabbedItems().size() > 0)
		{
			if (targetTile instanceof ISidedInventory)
			{
				ISidedInventory inventory = (ISidedInventory) targetTile;
				int[] slots = inventory.getAccessibleSlotsFromSide(direction.getOpposite().ordinal());
				Iterator<ItemStack> targetIt = this.tileEntity.getGrabbedItems().iterator();
				while (targetIt.hasNext())
				{
					ItemStack itemstack = targetIt.next();
					for (int i = 0; i < slots.length; i++)
					{
						if (this.stack == null || AutoCraftingManager.areStacksEqual(this.stack, itemstack))
						{
							if (inventory.canInsertItem(slots[i], itemstack, direction.getOpposite().ordinal()))
							{
								ItemStack slotStack = inventory.getStackInSlot(slots[i]);
								if (slotStack == null)
								{
									ItemStack insertstack = itemstack.copy();
									insertstack.stackSize = Math.min(itemstack.stackSize, inventory.getInventoryStackLimit());
									inventory.setInventorySlotContents(slots[i], insertstack);
									itemstack = AutoCraftingManager.decrStackSize(itemstack, insertstack.stackSize);
								}
								else if (AutoCraftingManager.areStacksEqual(slotStack, itemstack))
								{
									int room = slotStack.getMaxStackSize() - slotStack.stackSize;
									if (room > 0)
									{
										ItemStack insertstack = itemstack.copy();
										insertstack.stackSize = Math.min(Math.min(itemstack.stackSize, inventory.getInventoryStackLimit()), room);
										itemstack = AutoCraftingManager.decrStackSize(itemstack, insertstack.stackSize);
										insertstack.stackSize += slotStack.stackSize;
										inventory.setInventorySlotContents(slots[i], insertstack);
									}
								}
							}
							if (itemstack == null || itemstack.stackSize <= 0)
							{
								targetIt.remove();
								break;
							}
						}

					}

				}
				return false;
			}// TODO add a way to steal items from players

		}
		return true;
	}

	@Override
	public String toString()
	{
		return "give " + (stack != null ? stack.toString() : "1x???@???");
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
