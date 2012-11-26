package assemblyline.ai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.TileEntity;
import cpw.mods.fml.common.FMLLog;

public class TaskManager
{
	private final List<Task> tasks = new ArrayList<Task>();

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
			for (Task task : this.tasks)
			{
				if (task.getTickInterval() > 0)
				{
					if (this.ticks % task.getTickInterval() == 0)
					{
						if (!task.doTask())
						{
							task.onTaskEnd();
							tasks.remove(task);
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

	public void addTask(TileEntity tileEntity, Task task)
	{
		task.onTaskStart();
		tasks.add(task);
	}
}
