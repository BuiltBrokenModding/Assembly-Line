package dark.assembly.common.armbot.command;

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
    public void setExitPoint(int i, ITask task)
    {
        if (i == 0)
        {
            this.task = task;
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

}
