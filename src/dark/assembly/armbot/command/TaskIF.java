package dark.assembly.armbot.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.IProgrammableMachine;
import dark.api.al.coding.ITask;
import dark.api.al.coding.args.ArgumentData;
import dark.assembly.armbot.TaskBaseLogic;

/** @author DarkGuardsman */
public class TaskIF extends TaskBaseLogic
{
    protected ITask exitTruePoint = null;
    protected ITask exitFalsePoint = null;
    protected List<Vector2> exits = new ArrayList<Vector2>();
    protected Vector2 exitA, exitB;
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

    public TaskIF(Vector2 exitA, Vector2 exitB)
    {
        this();
        this.exitA = exitA;
        this.exitB = exitB;

    }

    @Override
    public void refresh()
    {
        super.refresh();
        if (this.getArg("check") != null && this.getArg("compare") != null)
        {
            this.isTrue = this.getArg("check").equals(this.getArg("compare"));
        }
        if (exitTruePoint == null && exitA != null)
        {
            exitTruePoint = this.program.getTaskAt(exitA);
        }
        if (exitFalsePoint == null && exitB != null)
        {
            exitFalsePoint = this.program.getTaskAt(exitB);
        }

        this.exits.clear();
        if (this.exitFalsePoint != null)
        {
            this.exits.add(this.exitFalsePoint.getPosition());
        }
        if (this.exitTruePoint != null)
        {
            this.exits.add(this.exitTruePoint.getPosition());
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

    @Override
    public List<Vector2> getExits()
    {
        return this.exits;
    }

}
