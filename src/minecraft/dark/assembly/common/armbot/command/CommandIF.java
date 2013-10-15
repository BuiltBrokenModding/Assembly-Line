package dark.assembly.common.armbot.command;

import universalelectricity.core.vector.Vector2;
import dark.api.al.armbot.IArmbotTask;
import dark.api.al.armbot.ISplitArmbotTask;
import dark.assembly.common.armbot.Command;

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

    public CommandIF(IArmbotTask entryPoint, IArmbotTask left, IArmbotTask right)
    {
        this();
        this.setEntryPoint(this.entryPoint);

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
        if(this.isTrue)
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



}
