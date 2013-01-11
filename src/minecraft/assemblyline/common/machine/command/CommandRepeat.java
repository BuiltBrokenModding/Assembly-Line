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
		this.tasksToRepeat = Math.max(this.getIntArg(0), 0);
	}

	@Override
	protected boolean doTask()
	{
		if (this.tasksToRepeat > 0)
		{
			this.commandManager.setCurrentTask(this.commandManager.getCurrentTask() - this.tasksToRepeat);
		}
		else
		{
			this.commandManager.setCurrentTask(0);
		}

		return false;
	}
}
