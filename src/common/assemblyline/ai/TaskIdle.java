package assemblyline.ai;

import assemblyline.machines.crafter.TileEntityCraftingArm;

public class TaskIdle extends Task
{
	public TaskIdle(TileEntityCraftingArm arm)
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
