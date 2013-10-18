package dark.assembly.common.armbot.command;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.ILogicTask;
import dark.api.al.coding.IProcessTask;
import dark.api.al.coding.IProgrammableMachine;
import dark.api.al.coding.ITask;
import dark.api.al.coding.args.ArgumentData;
import dark.assembly.common.armbot.TaskBaseLogic;
import dark.assembly.common.armbot.TaskBaseProcess;

/** @author DarkGuardsman */
public class TaskIF extends TaskBaseLogic
{
    protected ITask exitTruePoint = null;
    protected ITask exitFalsePoint = null;
    protected boolean isTrue = false;

    public TaskIF()
    {
        super("IF");
        this.defautlArguments.add(new ArgumentData("check", "statement"));
        this.defautlArguments.add(new ArgumentData("compare", "statement"));
    }

    public TaskIF(ITask trueExit, ITask falseExit)
    {
        this();
        this.exitTruePoint = trueExit;
        this.exitFalsePoint = falseExit;

    }

    @Override
    public void refresh()
    {
        super.refresh();
        if (this.getArg("check") != null && this.getArg("compare") != null)
        {
            this.isTrue = this.getArg("check").equals(this.getArg("compare"));
        }
    }

    @Override
    public TaskIF clone()
    {
        return new TaskIF();
    }

    @Override
    public ITask getExitPoint()
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
    public void setExitPoint(int i, ITask task)
    {
        if (i == 0)
        {
            this.exitFalsePoint = task;
        }
        else if (i == 1)
        {
            this.exitTruePoint = task;
        }
    }

    @Override
    public TaskIF load(NBTTagCompound nbt)
    {
        super.loadProgress(nbt);
        this.exitFalsePoint = this.program.getTaskAt(new Vector2(nbt.getDouble("exitFalseX"), (nbt.getDouble("exitFalseY"))));
        this.exitTruePoint = this.program.getTaskAt(new Vector2(nbt.getDouble("exitTrueX"), (nbt.getDouble("exitTrueY"))));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.saveProgress(nbt);
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
    public boolean canUseTask(IProgrammableMachine device)
    {
        return true;
    }

}
