package assemblyline.common.machine.command;


public class CommandDrop extends Command
{
	@Override
	protected boolean doTask()
	{
		super.doTask();

		// TODO: Animate Armbot to move down and drop all items.
		this.tileEntity.grabbedEntities.clear();
		return false;
	}
}
