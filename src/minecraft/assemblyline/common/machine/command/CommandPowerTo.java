package assemblyline.common.machine.command;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import dark.minecraft.helpers.DebugToPlayer;

public class CommandPowerTo extends Command
{
	private int times;
	private int curTimes;

	@Override
	public void onTaskStart()
	{
		this.times = 0;
		this.curTimes = 0;

		if (this.getArgs().length > 0)
		{
			this.times = this.getIntArg(0);
		}

		if (this.times <= 0)
			this.times = 1;
	}

	@Override
	protected boolean doTask()
	{
		Block block = Block.blocksList[this.world.getBlockId(tileEntity.getHandPosition().intX(), tileEntity.getHandPosition().intY(), tileEntity.getHandPosition().intZ())];
		TileEntity targetTile = this.tileEntity.getHandPosition().getTileEntity(this.world);

		if (tileEntity.getGrabbedEntities().size() > 0 && tileEntity.getGrabbedEntities().get(0) instanceof EntityItem && ((EntityItem) tileEntity.getGrabbedEntities().get(0)).getEntityItem().itemID == Block.torchRedstoneIdle.blockID)
		{
			// TODO have armbot cause redstone power at location
			DebugToPlayer.SendToClosest(this.tileEntity, 10, "powering");
		}
		else
		{
			return false;
		}

		this.curTimes++;

		if (this.curTimes >= this.times)
			return false;

		return true;
	}

	@Override
	public String toString()
	{
		return "POWERTO " + Integer.toString(this.times);
	}

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.times = taskCompound.getInteger("useTimes");
		this.curTimes = taskCompound.getInteger("useCurTimes");
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		taskCompound.setInteger("useTimes", this.times);
		taskCompound.setInteger("useCurTimes", this.curTimes);
	}
}
