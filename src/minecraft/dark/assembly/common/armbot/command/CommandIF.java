package dark.assembly.common.armbot.command;

import universalelectricity.core.vector.Vector2;
import net.minecraft.nbt.NBTTagCompound;
import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbotTask;
import dark.api.al.armbot.ISplitArmbotTask;

public class CommandIF extends Command implements ISplitArmbotTask
{
    protected IArmbotTask entryPoint = null;
    protected IArmbotTask exitTruePoint = null;
    protected IArmbotTask exitFalsePoint = null;
    protected boolean isTrue = false;

    public CommandIF()
    {
        super("IF");
    }

    public CommandIF(IArmbotTask entryPoint, IArmbotTask trueExit, IArmbotTask falseExit)
    {
        this();
        this.setEntryPoint(this.entryPoint);
        this.exitTruePoint = trueExit;
        this.exitFalsePoint = falseExit;

    }

    @Override
    public Command clone()
    {
        return new CommandIF(this.entryPoint, this.exitTruePoint, this.exitFalsePoint);
    }

    @Override
    public IArmbotTask getEntryPoint()
    {
        return this.entryPoint;
    }

    @Override
    public IArmbotTask getExitPoint()
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
    public ISplitArmbotTask setEntryPoint(IArmbotTask task)
    {
        this.entryPoint = task;
        return this;
    }

    @Override
    public void addExitPoint(IArmbotTask task)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Command readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.entryPoint = this.program.getTaskAt(new Vector2(nbt.getDouble("entryX"), (nbt.getDouble("entryY"))));
        this.exitFalsePoint = this.program.getTaskAt(new Vector2(nbt.getDouble("exitFalseX"), (nbt.getDouble("exitFalseY"))));
        this.exitTruePoint = this.program.getTaskAt(new Vector2(nbt.getDouble("exitTrueX"), (nbt.getDouble("exitTrueY"))));
        return this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
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

}
