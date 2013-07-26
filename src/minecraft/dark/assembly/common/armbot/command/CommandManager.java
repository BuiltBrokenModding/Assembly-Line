package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import cpw.mods.fml.common.FMLLog;
import dark.assembly.common.armbot.TileEntityArmbot;

public class CommandManager
{
    private final List<Command> tasks = new ArrayList<Command>();

    private int ticks = 0;
    private int currentTask = 0;
    private int lastTask = -1;

    /** Must be called every tick by a tileEntity. */
    public void onUpdate()
    {
        /** Loop through each task and do them. */
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

                    if (!task.doTask())
                    {
                        int tempCurrentTask = this.currentTask;
                        task.onTaskEnd();
                        this.currentTask++;

                        // Repeat needs to be persistent
                        if (!(task instanceof CommandRepeat))
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

    /** Used to register Tasks for a TileEntity, executes onTaskStart for the Task after registering
     * it
     * 
     * @param tileEntity TE instance to register the task for
     * @param newCommand Task instance to register */
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

    /** @return true when there are tasks registered, false otherwise */
    public boolean hasTasks()
    {
        return tasks.size() > 0;
    }

    public List<Command> getCommands()
    {
        return tasks;
    }

    /** Resets the command manager. */
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

    public void readFromNBT(TileEntityArmbot tileEntity, NBTTagCompound nbt)
    {
        this.currentTask = nbt.getInteger("curTasks");
        this.lastTask = nbt.getInteger("lastTask");
        this.ticks = nbt.getInteger("ticks");
        if (nbt.getInteger("numTasks") > 0)
        {
            NBTTagList taskList = nbt.getTagList("commands");
            for (int i = 0; i < taskList.tagCount(); i++)
            {
                NBTTagCompound cmdTag = (NBTTagCompound) taskList.tagAt(i);
                try
                {
                    Class cmdClass = Class.forName(cmdTag.getString("commandClass"));
                    ArrayList<String> pars = new ArrayList<String>();
                    if (cmdTag.getInteger("numParameters") > 0)
                    {
                        NBTTagList parameters = cmdTag.getTagList("parameters");
                        for (int ii = 0; ii < parameters.tagCount(); ii++)
                        {
                            pars.add(((NBTTagString) parameters.tagAt(ii)).data);
                        }
                    }
                    Command cmd = getNewCommand(tileEntity, cmdClass, pars.toArray(new String[] {}));
                    cmd.readFromNBT((NBTTagCompound) cmdTag.getTag("customData"));
                }
                catch (ClassNotFoundException e)
                {
                    System.out.println("Error loading CommandManger: ");
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("numTasks", this.tasks.size());
        if (this.tasks.size() > 0)
        {
            NBTTagList taskList = new NBTTagList("tasks");
            for (int i = 0; i < this.tasks.size(); i++)
            {
                NBTTagCompound taskCompound = new NBTTagCompound("taskCompound");
                String cmdName = this.tasks.get(i).getClass().getName();
                if (cmdName != null && !cmdName.isEmpty())
                    taskCompound.setString("commandClass", cmdName);
                if (this.tasks.get(i).getArgs().length > 0)
                {
                    NBTTagList parameters = new NBTTagList("parameters");
                    for (String par : this.tasks.get(i).getArgs())
                    {
                        if (par != null && !par.isEmpty())
                            parameters.appendTag(new NBTTagString("parameter", par));
                    }
                    taskCompound.setTag("parameters", parameters);
                }
                taskCompound.setInteger("numParameters", this.tasks.get(i).getArgs().length);
                NBTTagCompound customData = new NBTTagCompound("customData");
                this.tasks.get(i).writeToNBT(customData);
                taskCompound.setCompoundTag("customData", customData);
                taskList.appendTag(taskCompound);
            }
            nbt.setTag("commands", taskList);
        }
        nbt.setInteger("curTask", this.currentTask);
        nbt.setInteger("lastTask", this.lastTask);
        nbt.setInteger("ticks", this.ticks);
    }
}
