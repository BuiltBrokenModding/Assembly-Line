package assemblyline.ai;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLLog;

import universalelectricity.prefab.TileEntityAdvanced;

public class TileEntityAI extends TileEntityAdvanced
{
	private final List<Task> tasks = new ArrayList<Task>();

	public void updateEntity()
	{
		super.updateEntity();

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
	}
	
	public void addTask(Task task)
	{
		task.setTileEntity(this);
		task.onTaskStart();
		tasks.add(task);
	}
}
