package assemblyline.common.machine.command;

import java.util.HashMap;

import net.minecraft.world.World;
import assemblyline.common.machine.armbot.TileEntityArmbot;

/**
 * An AI Commands that is used by TileEntities with AI.
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
		registerCommand("grab", CommandGrab.class);
		registerCommand("rotate", CommandRotate.class);
	}

	public static void registerCommand(String command, Class<? extends Command> commandClass)
	{
		COMMANDS.put(command, commandClass);
	}

	public static Class<? extends Command> getCommand(String command)
	{
		return COMMANDS.get(command);
	}

	/**
	 * The amount of ticks this command has been running for.
	 */
	protected int ticks;

	protected World world;
	protected TileEntityArmbot tileEntity;

	/**
	 * The parameters this command has, or the properties. Entered by the player in the disk.
	 */
	protected String[] parameters;

	public Command(TileEntityArmbot arm, String... parameters)
	{
		this.tileEntity = arm;
		this.world = tileEntity.worldObj;
		this.parameters = parameters;
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
