package assemblyline.common.machine.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import assemblyline.common.machine.armbot.TileEntityArmbot;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLLog;

public class CommandManager
{
	private final List<Command> tasks = new ArrayList<Command>();

	private int ticks = 0;
	private int currentTask = 0;
	private int lastTask = -1;

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
			if (this.tasks.size() > 0)
			{
				if (this.currentTask < this.tasks.size())
				{
					if (this.currentTask < 0)
					{
						this.currentTask = 0;
					}

					Command task = this.tasks.get(this.currentTask);

					if (this.currentTask != this.lastTask)
					{
						this.lastTask = this.currentTask;
						task.onTaskStart();
					}

					if (!task.doTask())
					{
						task.onTaskEnd();
						this.currentTask++;
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
	public void addTask(TileEntityArmbot tileEntity, Command task, String[] parameters)
	{
		task.world = tileEntity.worldObj;
		task.tileEntity = tileEntity;
		task.commandManager = this;
		task.setParameters(parameters);
		this.tasks.add(task);
		task.onTaskStart();
	}

	public void addTask(TileEntityArmbot tileEntity, Command task)
	{
		this.addTask(tileEntity, task, new String[0]);
	}

	/**
	 * @return true when there are tasks registered, false otherwise
	 */
	public boolean hasTasks()
	{
		return !tasks.isEmpty();
	}

	public List<Command> getCommands()
	{
		return tasks;
	}

	public void clearTasks()
	{
		this.tasks.clear();
	}

	public void setCurrentTask(int i)
	{
		this.currentTask = Math.max(Math.min(i, this.tasks.size() - 1), 0);
	}

	public int getCurrentTask()
	{
		return this.currentTask;
	}
}
