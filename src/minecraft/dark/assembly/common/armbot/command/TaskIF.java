package dark.assembly.common.armbot.command;

import universalelectricity.core.vector.Vector2;
import net.minecraft.nbt.NBTTagCompound;
import dark.api.al.coding.IDeviceTask;
import dark.api.al.coding.ILogicDevice;
import dark.api.al.coding.ISplitArmbotTask;
import dark.assembly.common.armbot.TaskBase;

public class TaskIF extends TaskBase implements ISplitArmbotTask
{
    protected IDeviceTask entryPoint = null;
    protected IDeviceTask exitTruePoint = null;
    protected IDeviceTask exitFalsePoint = null;
    protected boolean isTrue = false;

    public TaskIF()
    {
        super("IF", TaskType.DECISION);
    }

    public TaskIF(IDeviceTask entryPoint, IDeviceTask trueExit, IDeviceTask falseExit)
    {
        this();
        this.setEntryPoint(this.entryPoint);
        this.exitTruePoint = trueExit;
        this.exitFalsePoint = falseExit;

    }

    @Override
    public TaskBase clone()
    {
        return new TaskIF(this.entryPoint, this.exitTruePoint, this.exitFalsePoint);
    }

    @Override
    public IDeviceTask getEntryPoint()
    {
        return this.entryPoint;
    }

    @Override
    public IDeviceTask getExitPoint()
    {
        if (this.isTrue)
        {
            return this.exitTruePoint;
        }
        return this.exitFalsePoint;
    }

    @Override
    public int getMaxExitPoints()
    {
        return 2;
    }

    @Override
    public ISplitArmbotTask setEntryPoint(IDeviceTask task)
    {
        this.entryPoint = task;
        return this;
    }

    @Override
    public void addExitPoint(IDeviceTask task)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public TaskBase load(NBTTagCompound nbt)
    {
        super.loadProgress(nbt);
        this.entryPoint = this.program.getTaskAt(new Vector2(nbt.getDouble("entryX"), (nbt.getDouble("entryY"))));
        this.exitFalsePoint = this.program.getTaskAt(new Vector2(nbt.getDouble("exitFalseX"), (nbt.getDouble("exitFalseY"))));
        this.exitTruePoint = this.program.getTaskAt(new Vector2(nbt.getDouble("exitTrueX"), (nbt.getDouble("exitTrueY"))));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.saveProgress(nbt);
        if (this.entryPoint != null)
        {
            nbt.setDouble("entryX", this.entryPoint.getPosition().x);
            nbt.setDouble("entryY", this.entryPoint.getPosition().y);
        }
        if (this.exitFalsePoint != null)
        {
            nbt.setDouble("exitFalseX", this.exitFalsePoint.getPosition().x);
            nbt.setDouble("exitFalseY", this.exitFalsePoint.getPosition().y);
        }
        if (this.exitTruePoint != null)
        {
            nbt.setDouble("exitTrueX", this.exitTruePoint.getPosition().x);
            nbt.setDouble("exitTrueY", this.exitTruePoint.getPosition().y);
        }
        return nbt;
    }

    @Override
    public boolean canUseTask(ILogicDevice device)
    {
        return true;
    }

}
