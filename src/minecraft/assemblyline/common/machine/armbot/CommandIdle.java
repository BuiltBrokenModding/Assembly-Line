package assemblyline.common.machine.armbot;

import assemblyline.common.machine.crafter.TileEntityArmbot;

public class CommandIdle extends Command
{
	public CommandIdle(TileEntityArmbot arm)
	{
		super(arm);
	}

	protected boolean doTask()
	{
		/**
		 * randomly move the arm to similate life in the arm if the arm is powered
		 */
		return true;

	}

}
