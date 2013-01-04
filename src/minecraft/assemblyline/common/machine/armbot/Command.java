package assemblyline.common.machine.armbot;

import java.util.HashMap;

import assemblyline.common.machine.crafter.TileEntityArmbot;

/**
 * An AI Task that is used by TileEntities with AI.
 * 
 * @author Calclavia
 * 
 */
public abstract class Command
{
	/**
	 * A class of all available commands.
	 * 
	 * String - Command name. Command - The actual command class.
	 */
	private static final HashMap<String, Class> COMMANDS = new HashMap<String, Class>();

	static
	{
		registerCommand("idle", CommandIdle.class);
	}

	public static void registerCommand(String command, Class<? extends Command> commandClass)
	{
		COMMANDS.put(command, commandClass);
	}

	public static Class<? extends Command> getCommand(String command)
	{
		return COMMANDS.get(command);
	}

	protected int ticks;
	protected TileEntityArmbot tileEntity;

	public Command(TileEntityArmbot arm)
	{
		this.tileEntity = arm;
	}

	/**
	 * Called by the TaskManager to propagate tick updates
	 * 
	 * @param ticks The amount of ticks this task has been running
	 * @return false if the task is finished and can be removed, true otherwise
	 */
	protected boolean doTask()
	{
		this.ticks++;
		return true;
	}

	public void onTaskStart()
	{
	}

	public void onTaskEnd()
	{
	}

	/**
	 * @return The tick interval of this task. 0 means it will receive no update ticks.
	 */
	public int getTickInterval()
	{
		return 0;
	}
}
