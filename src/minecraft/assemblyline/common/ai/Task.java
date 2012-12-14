package assemblyline.common.ai;

import assemblyline.common.machine.crafter.TileEntityCraftingArm;

/**
 * An AI Task that is used by TileEntities with AI.
 * 
 * @author Calclavia
 * 
 */
public abstract class Task
{
	protected int ticks;
	protected TileEntityCraftingArm tileEntity;

	public Task(TileEntityCraftingArm arm)
	{
		this.tileEntity = arm;
	}

	/**
	 * Called by the TaskManager to propagate tick updates
	 * 
	 * @param ticks
	 *            The amount of ticks this task has been running
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
