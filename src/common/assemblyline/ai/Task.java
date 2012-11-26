package assemblyline.ai;

import net.minecraft.src.TileEntity;
import assemblyline.machines.crafter.TileEntityCraftingArm;

/**
 * An AI Task that is used by TileEntities with
 * AI.
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
	 * Called when a task is being done.
	 * 
	 * @param ticks
	 *            The amount of ticks this task
	 *            has been elapsed for.
	 * @return Return true if the task is not
	 *         finished and false if it is.
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
	 * @return The tick interval of this task.
	 *         Return 0 for no ticks.
	 */
	public int getTickInterval()
	{
		return 1;
	}
}
