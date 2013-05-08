package assemblyline.common.armbot.command;

/**
 * Used by arms to break a specific block in a position.
 * 
 * @author Calclavia
 */
public class CommandHarvest extends CommandBreak
{
	private CommandRotateTo rotateToCommand;

	@Override
	public void onTaskStart()
	{
		this.keep = true;
	}

	@Override
	public String toString()
	{
		return "HARVEST";
	}
}
