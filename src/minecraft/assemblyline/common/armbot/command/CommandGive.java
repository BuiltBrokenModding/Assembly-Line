package assemblyline.common.armbot.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import assemblyline.common.machine.InvInteractionHelper;

public class CommandGive extends Command
{
	private ItemStack stack;
	private int ammount = -1;

	@Override
	public void onTaskStart()
	{
		int id = 0;
		int meta = 32767;

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
			ammount = this.getIntArg(1);
		}
		if (id == 0)
		{
			stack = null;
		}
		else
		{
			stack = new ItemStack(id, ammount == -1 ? 1 : ammount, meta);
		}
	}

	@Override
	protected boolean doTask()
	{
		TileEntity targetTile = this.tileEntity.getHandPosition().getTileEntity(this.world);

		if (targetTile != null && this.tileEntity.getGrabbedItems().size() > 0)
		{
			ForgeDirection direction = this.tileEntity.getFacingDirectionFromAngle();
			List<ItemStack> stacks = new ArrayList<ItemStack>();
			if (this.stack != null)
			{
				stacks.add(stack);
			}
			InvInteractionHelper invEx = new InvInteractionHelper(this.tileEntity.worldObj, new Vector3(this.tileEntity), stacks, false);

			Iterator<ItemStack> targetIt = this.tileEntity.getGrabbedItems().iterator();
			boolean flag = true;
			while (targetIt.hasNext())
			{
				ItemStack insertStack = targetIt.next();
				if (insertStack != null)
				{
					ItemStack original = insertStack.copy();
					insertStack = invEx.tryPlaceInPosition(insertStack, new Vector3(targetTile), direction.getOpposite());
					flag = insertStack != null && insertStack.stackSize == original.stackSize;
					if (insertStack == null || insertStack.stackSize <= 0)
					{
						targetIt.remove();
						break;
					}
				}
			}
			return flag;
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
