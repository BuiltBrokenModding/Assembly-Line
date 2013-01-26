package assemblyline.common.machine.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import assemblyline.common.machine.armbot.TileEntityArmbot;
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
						this.lastTask = -1;
					}

					Command task = this.tasks.get(this.currentTask);

					if (this.currentTask != this.lastTask)
					{
						this.lastTask = this.currentTask;
						task.onTaskStart();
					}

					// if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
					// System.out.println("curTask: " + this.currentTask + ": " +
					// this.tasks.get(this.currentTask).toString().substring(this.tasks.get(this.currentTask).toString().lastIndexOf('.')
					// + 1));

					if (!task.doTask())
					{
						int tempCurrentTask = this.currentTask;
						task.onTaskEnd();
						this.currentTask++;
						if (!(task instanceof CommandRepeat)) // repeat needs to be persistent
						{
							// End the task and reinitialize it into a new class to make sure it is
							// fresh.
							this.tasks.set(tempCurrentTask, this.getNewCommand(task.tileEntity, task.getClass(), task.getArgs()));
						}
					}
				}
				else
				{
					this.clear();
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

	public Command getNewCommand(TileEntityArmbot tileEntity, Class<? extends Command> commandClass, String[] parameters)
	{
		try
		{
			Command newCommand = commandClass.newInstance();
			newCommand.world = tileEntity.worldObj;
			newCommand.tileEntity = tileEntity;
			newCommand.commandManager = this;
			newCommand.setParameters(parameters);
			return newCommand;
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to add command");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Used to register Tasks for a TileEntity, executes onTaskStart for the Task after registering
	 * it
	 * 
	 * @param tileEntity TE instance to register the task for
	 * @param newCommand Task instance to register
	 */
	public void addCommand(TileEntityArmbot tileEntity, Class<? extends Command> commandClass, String[] parameters)
	{
		Command newCommand = this.getNewCommand(tileEntity, commandClass, parameters);

		if (newCommand != null)
		{
			this.tasks.add(newCommand);
		}
	}

	public void addCommand(TileEntityArmbot tileEntity, Class<? extends Command> task)
	{
		this.addCommand(tileEntity, task, new String[0]);
	}

	/**
	 * @return true when there are tasks registered, false otherwise
	 */
	public boolean hasTasks()
	{
		return tasks.size() > 0;
	}

	public List<Command> getCommands()
	{
		return tasks;
	}

	/**
	 * Resets the command manager.
	 */
	public void clear()
	{
		this.tasks.clear();
		this.currentTask = 0;
		this.lastTask = -1;
		this.ticks = 0;
	}

	public void setCurrentTask(int i)
	{
		this.currentTask = Math.min(i, this.tasks.size());
	}

	public int getCurrentTask()
	{
		return this.currentTask;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList taskList = new NBTTagList();
		for (int i = 0; i < this.tasks.size(); i++)
		{
			NBTTagCompound taskCompound = new NBTTagCompound();
			this.tasks.get(i).writeToNBT(taskCompound);
			taskList.appendTag(taskCompound);
		}
	}
}
