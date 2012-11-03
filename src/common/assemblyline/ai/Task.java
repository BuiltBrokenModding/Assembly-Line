package assemblyline.ai;

import net.minecraft.src.TileEntity;

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

	/**
	 * The TileEntity that is doing this task.
	 */
	public TileEntity handler;

	private boolean shouldExecute = true;

	public Task(TileEntity handler)
	{
		this.handler = handler;
	}

	/**
	 * Called when a task is being done.
	 * 
	 * @param ticks
	 *            - The amount of ticks this task
	 *            has been elapsed for.
	 */
	protected void doTask()
	{
		this.ticks++;
	}

	public void resetTask()
	{
	}

	/**
	 * @return Whether the task should keep
	 *         executing.
	 */
	public boolean shouldExecute()
	{
		return this.shouldExecute;
	}
}
