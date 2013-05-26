package assemblyline.common.armbot.command;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import assemblyline.api.IArmbotUseable;

public class CommandUse extends Command
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

		if (targetTile != null)
		{
			if (targetTile instanceof IArmbotUseable)
			{
				((IArmbotUseable) targetTile).onUse(this.tileEntity, this.getArgs());
			}

		}
		else if (block != null)
		{
			try
			{
				boolean f = block.onBlockActivated(this.world, tileEntity.getHandPosition().intX(), tileEntity.getHandPosition().intY(), tileEntity.getHandPosition().intZ(), null, 0, 0, 0, 0);
			}
			catch (Exception e)
			{

				e.printStackTrace();
			}

		}

		this.curTimes++;

		if (this.curTimes >= this.times)
		{
			return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		return "USE " + Integer.toString(this.times);
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
