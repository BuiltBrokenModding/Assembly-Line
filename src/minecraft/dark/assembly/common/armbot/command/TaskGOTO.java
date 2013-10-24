package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.IRedirectTask;
import dark.api.al.coding.ITask;
import dark.assembly.common.armbot.TaskBase;

/** @author DarkGuardsman */
public class TaskGOTO extends TaskBase implements IRedirectTask
{
    protected ITask task;
    protected Vector2 taskPos;
    protected boolean render = true;
    protected List<Vector2> exits = new ArrayList<Vector2>();

    public TaskGOTO()
    {
        super("GoTo", TaskType.DECISION);
    }

    public TaskGOTO(boolean render)
    {
        this();
        this.render = render;
    }

    @Override
    public ITask getExitPoint()
    {
        return task;
    }

    @Override
    public void refresh()
    {
        super.refresh();
        if(task == null && taskPos != null)
        {
            this.task = this.program.getTaskAt(taskPos);
        }
        this.exits.clear();
        if (this.task != null)
        {
            this.exits.add(this.task.getPosition());
        }
    }

    @Override
    public void setExitPoint(int i, ITask task)
    {
        if (i == 0)
        {
            this.task = task;
        }
    }

    public void setExitPoint(int i, Vector2 vector2)
    {
        if (i == 0)
        {
            this.taskPos = vector2;
        }
    }

    @Override
    public int getMaxExitPoints()
    {
        return 1;
    }

    @Override
    public boolean render()
    {
        return this.render;
    }

    @Override
    public TaskGOTO load(NBTTagCompound nbt)
    {
        super.loadProgress(nbt);
        this.taskPos = new Vector2(nbt.getDouble("entryX"), (nbt.getDouble("entryY")));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.saveProgress(nbt);
        if (this.task != null)
        {
            nbt.setDouble("entryX", this.task.getPosition().x);
            nbt.setDouble("entryY", this.task.getPosition().y);
        }
        return nbt;
    }

    @Override
    public TaskBase clone()
    {
        return new TaskGOTO();
    }

    @Override
    public List<Vector2> getExits()
    {
        // TODO Auto-generated method stub
        return exits;
    }

}
