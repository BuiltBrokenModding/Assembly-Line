package dark.assembly.common.armbot;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dark.api.al.coding.IArmbot;
import dark.api.al.coding.IProgramableMachine;

public abstract class TaskArmbot extends TaskBase
{
    /** Armbot instance */
    protected IArmbot armbot;

    public TaskArmbot(String name, TaskType tasktype)
    {
        super(name, tasktype);
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        super.onMethodCalled(world, location, armbot);
        if (armbot instanceof IArmbot)
        {
            this.armbot = (IArmbot) armbot;

            return ProcessReturn.CONTINUE;
        }
        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public Object[] onCCMethodCalled(World world, Vector3 location, IProgramableMachine armbot, IComputerAccess computer, ILuaContext context) throws Exception
    {
        super.onCCMethodCalled(world, location, armbot, computer, context);
        if (armbot instanceof IArmbot)
        {
            this.armbot = (IArmbot) armbot;
        }

        return null;
    }

    @Override
    public boolean canUseTask(IProgramableMachine device)
    {
        return device instanceof IArmbot;
    }

}
