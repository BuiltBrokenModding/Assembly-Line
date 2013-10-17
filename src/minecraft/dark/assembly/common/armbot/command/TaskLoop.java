package dark.assembly.common.armbot.command;

import com.builtbroken.common.science.units.UnitHelper;

import universalelectricity.core.vector.Vector3;
import dark.api.al.coding.IDeviceTask;
import dark.api.al.coding.ILogicDevice;
import dark.api.al.coding.ISplitArmbotTask;
import dark.api.al.coding.IDeviceTask.TaskType;
import dark.assembly.common.armbot.TaskBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Basic While loop that mainly handles number of repeats.
 *
 * @author DarkGuardsman */
public class TaskLoop extends TaskBase implements ISplitArmbotTask
{
    protected int numReps = -1;
    protected IDeviceTask entry, exit;

    public TaskLoop()
    {
        super("repeat", TaskType.DECISION);
    }

    public TaskLoop(IDeviceTask entry, IDeviceTask exit)
    {
        this();
        this.entry = entry;
        this.exit = exit;
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        super.onMethodCalled(world, location, armbot);
        this.numReps = UnitHelper.tryToParseInt(this.getArg(0), -1);
        return ProcessReturn.CONTINUE;
    }

    @Override
    public boolean canUseTask(ILogicDevice device)
    {
        return true;
    }

    @Override
    public IDeviceTask getEntryPoint()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDeviceTask getExitPoint()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMaxExitPoints()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ISplitArmbotTask setEntryPoint(IDeviceTask task)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addExitPoint(IDeviceTask task)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public TaskBase clone()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDeviceTask loadProgress(NBTTagCompound nbt)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound nbt)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
