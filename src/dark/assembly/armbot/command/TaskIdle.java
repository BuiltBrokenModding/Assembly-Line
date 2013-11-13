package dark.assembly.armbot.command;

import net.minecraft.nbt.NBTTagCompound;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.coding.IProgrammableMachine;
import dark.api.al.coding.args.ArgumentData;
import dark.assembly.armbot.TaskBaseProcess;

public class TaskIdle extends TaskBaseProcess
{

    /** The amount of time in which the machine will idle. */
    public int idleTime = 80;
    private int totalIdleTime = 80;

    public TaskIdle()
    {
        super("wait");
        this.defautlArguments.add(new ArgumentData("idleTime", 20));
    }

    @Override
    public ProcessReturn onMethodCalled()
    {
        if (super.onMethodCalled() == ProcessReturn.CONTINUE)
        {

            if (UnitHelper.tryToParseInt(this.getArg("idleTime")) > 0)
            {
                this.totalIdleTime = this.idleTime = UnitHelper.tryToParseInt(this.getArg("idleTime"));
                return ProcessReturn.CONTINUE;
            }

            return ProcessReturn.ARGUMENT_ERROR;
        }
        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (this.idleTime > 0)
        {
            this.idleTime--;
            return ProcessReturn.CONTINUE;
        }
        return ProcessReturn.DONE;
    }

    @Override
    public TaskBaseProcess loadProgress(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.idleTime = taskCompound.getInteger("idleTime");
        this.totalIdleTime = taskCompound.getInteger("idleTotal");
        return this;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound taskCompound)
    {
        super.saveProgress(taskCompound);
        taskCompound.setInteger("idleTime", this.idleTime);
        taskCompound.setInteger("idleTotal", this.totalIdleTime);
        return taskCompound;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + Integer.toString(this.totalIdleTime);
    }

    @Override
    public TaskBaseProcess clone()
    {
        return new TaskIdle();
    }

    @Override
    public boolean canUseTask(IProgrammableMachine device)
    {
        return true;
    }

}
