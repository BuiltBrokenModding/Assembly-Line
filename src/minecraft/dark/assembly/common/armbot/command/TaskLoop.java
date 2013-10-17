package dark.assembly.common.armbot.command;

import com.builtbroken.common.science.units.UnitHelper;

import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import dark.api.al.coding.IDeviceTask;
import dark.api.al.coding.IProgramableMachine;
import dark.api.al.coding.ISplitArmbotTask;
import dark.api.al.coding.IDeviceTask.TaskType;
import dark.api.al.coding.args.ArgumentIntData;
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
        this.defautlArguments.add(new ArgumentIntData("loop", 1, Integer.MAX_VALUE, -1));
    }

    public TaskLoop(IDeviceTask entry, IDeviceTask exit)
    {
        this();
        this.entry = entry;
        this.exit = exit;
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        super.onMethodCalled(world, location, armbot);
        this.numReps = UnitHelper.tryToParseInt(this.getArg("loop"), 1);
        return ProcessReturn.CONTINUE;
    }

    @Override
    public boolean canUseTask(IProgramableMachine device)
    {
        return true;
    }

    @Override
    public IDeviceTask getEntryPoint()
    {
        return this.entry;
    }

    @Override
    public IDeviceTask getExitPoint()
    {
        return this.exit;
    }

    @Override
    public int getMaxExitPoints()
    {
        return 1;
    }

    @Override
    public ISplitArmbotTask setEntryPoint(IDeviceTask task)
    {
        this.entry = task;
        return this;
    }

    @Override
    public void addExitPoint(IDeviceTask task)
    {
        this.exit = task;
    }

    @Override
    public TaskBase clone()
    {
        return new TaskLoop();
    }

    @Override
    public TaskBase load(NBTTagCompound nbt)
    {
        super.loadProgress(nbt);
        this.entry = this.program.getTaskAt(new Vector2(nbt.getDouble("entryX"), (nbt.getDouble("entryY"))));
        this.exit = this.program.getTaskAt(new Vector2(nbt.getDouble("exitX"), (nbt.getDouble("exitY"))));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.saveProgress(nbt);
        if (this.entry != null)
        {
            nbt.setDouble("entryX", this.entry.getPosition().x);
            nbt.setDouble("entryY", this.entry.getPosition().y);
        }
        if (this.exit != null)
        {
            nbt.setDouble("exitX", this.exit.getPosition().x);
            nbt.setDouble("exitY", this.exit.getPosition().y);
        }
        return nbt;
    }
}
