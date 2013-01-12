package assemblyline.common.machine.command;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
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
	private static final HashMap<Class, String> REVERSE_LOOKUP = new HashMap<Class, String>();

	static
	{
		registerCommand("idle", CommandIdle.class);
		registerCommand("grab", CommandGrab.class);
		registerCommand("drop", CommandDrop.class);
		registerCommand("rotate", CommandRotate.class);
		registerCommand("return", CommandReturn.class);
		registerCommand("repeat", CommandRepeat.class);
	}

	public static void registerCommand(String command, Class<? extends Command> commandClass)
	{
		COMMANDS.put(command, commandClass);
		REVERSE_LOOKUP.put(commandClass, command);
	}

	public static Class<? extends Command> getCommand(String command)
	{
		return COMMANDS.get(command.toLowerCase());
	}
	
	public static String getCommandName(Class<? extends Command> command)
	{
		return REVERSE_LOOKUP.get(command);
	}

	/**
	 * The amount of ticks this command has been running for.
	 */
	protected int ticks = 0;

	public World world;
	public TileEntityArmbot tileEntity;
	public CommandManager commandManager;

	/**
	 * The parameters this command has, or the properties. Entered by the player in the disk.
	 * Parameters are entered like a Java function. idle(20) = Idles for 20 seconds.
	 */
	private String[] parameters;

	/**
	 * Called by the TaskManager to propagate tick updates
	 * 
	 * @param ticks The amount of ticks this task has been running
	 * @return false if the task is finished and can be continued, true otherwise
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
		return 1;
	}

	public void setParameters(String[] strings)
	{
		this.parameters = strings;
	}

	/**
	 * Some functions to help get parameter arguments.
	 */
	protected String getArg(int i)
	{
		if (i >= 0 && i < this.parameters.length) { return this.parameters[i]; }

		return null;
	}

	protected int getIntArg(int i)
	{
		if (getArg(i) != null)
		{
			try
			{
				return Integer.parseInt(getArg(i));
			}
			catch (Exception e)
			{

			}
		}

		return 0;
	}

	public void writeToNBT(NBTTagCompound taskCompound)
	{
		
	}
}
