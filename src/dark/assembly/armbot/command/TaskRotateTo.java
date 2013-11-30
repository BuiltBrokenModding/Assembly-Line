package dark.assembly.armbot.command;

import universalelectricity.core.vector.Vector2;
import net.minecraft.nbt.NBTTagCompound;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.coding.IArmbot;
import dark.api.al.coding.args.ArgumentIntData;
import dark.assembly.armbot.TaskBaseArmbot;
import dark.assembly.armbot.TaskBaseProcess;
import dark.core.helpers.MathHelper;

/** Rotates the armbot to a specific direction. If not specified, it will turn right.
 *
 * @author DarkGuardsman */
public class TaskRotateTo extends TaskBaseArmbot
{
    int targetRotationYaw = 0, targetRotationPitch = 0, currentRotationYaw, currentRotationPitch;

    public TaskRotateTo()
    {
        super("RotateTo");
        this.defautlArguments.add(new ArgumentIntData("yaw", 0, 360, 0));
        this.defautlArguments.add(new ArgumentIntData("pitch", 0, 360, 0));
        this.UV = new Vector2(100, 80);
    }

    public TaskRotateTo(int yaw, int pitch)
    {
        this();
        this.targetRotationYaw = yaw;
        this.targetRotationPitch = pitch;
        this.setArg("yaw", yaw);
        this.setArg("pitch", pitch);
    }

    @Override
    public ProcessReturn onMethodCalled()
    {
        if (super.onMethodCalled() == ProcessReturn.CONTINUE)
        {

            this.targetRotationYaw = (int) MathHelper.clampAngleTo360(UnitHelper.tryToParseInt(this.getArg("yaw")));
            this.targetRotationPitch = (int) MathHelper.clampAngleTo360(UnitHelper.tryToParseInt(this.getArg("pitch")));

            return ProcessReturn.CONTINUE;
        }
        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (super.onUpdate() == ProcessReturn.CONTINUE)
        {
            ((IArmbot) this.program.getMachine()).moveArmTo(this.targetRotationYaw, this.targetRotationPitch);

            return Math.abs(((IArmbot) this.program.getMachine()).getRotation().y - this.targetRotationPitch) > 0 && Math.abs(((IArmbot) this.program.getMachine()).getRotation().x - this.targetRotationYaw) > 0 ? ProcessReturn.CONTINUE : ProcessReturn.DONE;
        }
        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + Float.toString(this.targetRotationYaw) + " " + Float.toString(this.targetRotationPitch);
    }

    @Override
    public TaskRotateTo load(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.targetRotationPitch = taskCompound.getInteger("rotPitch");
        this.targetRotationYaw = taskCompound.getInteger("rotYaw");
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound taskCompound)
    {
        super.saveProgress(taskCompound);
        taskCompound.setInteger("rotPitch", this.targetRotationPitch);
        taskCompound.setInteger("rotYaw", this.targetRotationYaw);
        return taskCompound;
    }

    @Override
    public TaskBaseProcess clone()
    {
        return new TaskRotateTo();
    }
}
