package assemblyline.common.machine.command;

public class CommandDrop extends Command
{
	@Override
	protected boolean doTask()
	{
		super.doTask();

		this.tileEntity.dropAll();
		this.world.playSound(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, "random.pop", 0.2F, ((this.tileEntity.worldObj.rand.nextFloat() - this.tileEntity.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F, true);

		return false;
	}

	@Override
	public String toString()
	{
		return "DROP";
	}
}
