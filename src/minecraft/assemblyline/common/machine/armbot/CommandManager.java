package assemblyline.common.machine.armbot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLLog;

public class CommandManager
{
	private final List<Command> tasks = new ArrayList<Command>();

	private int ticks = 0;

	/**
	 * Must be called every tick by a tileEntity.
	 */
	public void onUpdate()
	{
		/**
		 * Loop through each task and do them.
		 */
		try
		{
			Command task;
			Iterator<Command> iter = tasks.iterator();
			while (iter.hasNext())
			{
				task = iter.next();
				if (task.getTickInterval() > 0)
				{
					if (this.ticks % task.getTickInterval() == 0)
					{
						if (!task.doTask())
						{
							task.onTaskEnd();
							iter.remove();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to execute task in Assembly Line.");
			e.printStackTrace();
		}

		this.ticks++;
	}

	/**
	 * Used to register Tasks for a TileEntity, executes onTaskStart for the Task after registering
	 * it
	 * 
	 * @param tileEntity TE instance to register the task for
	 * @param task Task instance to register
	 */
	public void addTask(TileEntity tileEntity, Command task)
	{
		tasks.add(task);
		task.onTaskStart();
	}

	/**
	 * @return true when there are tasks registered, false otherwise
	 */
	public boolean hasTasks()
	{
		return !tasks.isEmpty();
	}
}
