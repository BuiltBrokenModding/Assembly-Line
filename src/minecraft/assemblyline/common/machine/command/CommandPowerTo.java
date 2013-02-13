package assemblyline.common.machine.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import dark.minecraft.helpers.DebugToPlayer;
import dark.minecraft.helpers.ItemWorldHelper;

public class CommandPowerTo extends Command
{
	private int duration;
	private int ticksRan;

	@Override
	public void onTaskStart()
	{
		this.duration = 0;
		this.ticksRan = 0;

		if (this.getArgs().length > 0)
		{
			this.duration = this.getIntArg(0);
		}

		if (this.duration <= 0)
		{
			this.duration = 20;
		}
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();
		if (this.ticksRan >= duration)
		{
			powerBlock(false);
			return false;
		}

		Block block = Block.blocksList[this.world.getBlockId(tileEntity.getHandPosition().intX(), tileEntity.getHandPosition().intY(), tileEntity.getHandPosition().intZ())];
		TileEntity targetTile = this.tileEntity.getHandPosition().getTileEntity(this.world);

		if (this.tileEntity.getGrabbedItems().size() > 0)
		{
			List<ItemStack> stacks = new ArrayList<ItemStack>();
			stacks.add(new ItemStack(Block.torchRedstoneActive, 1, 0));
			stacks.add(new ItemStack(Block.torchRedstoneIdle, 1, 0));
			if (ItemWorldHelper.filterItems(this.tileEntity.getGrabbedItems(), stacks).size() > 0)
			{
				this.powerBlock(true);
			}
		}

		this.ticksRan++;
		return true;
	}

	public void powerBlock(boolean on)
	{
		if (!on)
		{
			// unpower block
			//DebugToPlayer.SendToClosest(this.tileEntity, 20, "Power Off");
		}
		else
		{
			// power block
			//DebugToPlayer.SendToClosest(this.tileEntity, 20, "Power on");
		}
	}

	@Override
	public String toString()
	{
		return "POWERTO " + Integer.toString(this.duration);
	}

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.duration = taskCompound.getInteger("useTimes");
		this.ticksRan = taskCompound.getInteger("useCurTimes");
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		taskCompound.setInteger("useTimes", this.duration);
		taskCompound.setInteger("useCurTimes", this.ticksRan);
	}
}
