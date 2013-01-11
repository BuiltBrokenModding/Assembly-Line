package assemblyline.common.machine.command;

/**
 * This task resets all previous tasks and does them again in a loop.
 * 
 * @author Calclavia
 * 
 */
public class CommandRepeat extends Command
{
	/**
	 * The amount of tasks above this task to repeat.
	 */
	private int tasksToRepeat;

	public void onTaskStart()
	{
		this.tasksToRepeat = this.getIntArg(0);
	}

	@Override
	protected boolean doTask()
	{
		this.commandManager.setCurrentTask(this.commandManager.getCurrentTask() - this.tasksToRepeat);
		return false;
	}
}
