package dark.assembly.armbot.command;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector2;

import com.builtbroken.common.science.units.UnitHelper;
import com.dark.helpers.MathHelper;

import dark.api.al.coding.IArmbot;
import dark.api.al.coding.args.ArgumentIntData;
import dark.assembly.armbot.TaskBaseArmbot;
import dark.assembly.armbot.TaskBaseProcess;

/** Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author DarkGuardsman */
public class TaskRotateTo extends TaskBaseArmbot
{
    int targetRotationYaw = 0, targetRotationPitch = 0, currentRotationYaw, currentRotationPitch;

    public TaskRotateTo()
    {
        super("RotateTo");
        this.args.add(new ArgumentIntData("yaw", 0, 360, 0));
        this.args.add(new ArgumentIntData("pitch", 0, 360, 0));
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
    public void load(NBTTagCompound taskCompound)
    {
        super.load(taskCompound);
        this.targetRotationPitch = taskCompound.getInteger("rotPitch");
        this.targetRotationYaw = taskCompound.getInteger("rotYaw");
    }

    @Override
    public void save(NBTTagCompound taskCompound)
    {
        super.save(taskCompound);
        taskCompound.setInteger("rotPitch", this.targetRotationPitch);
        taskCompound.setInteger("rotYaw", this.targetRotationYaw);
    }

    @Override
    public TaskBaseProcess clone()
    {
        return new TaskRotateTo();
    }

    @Override
    public void getToolTips(List<String> list)
    {
        super.getToolTips(list);
        list.add(" Yaw:   " + this.targetRotationYaw);
        list.add(" Pitch: " + this.targetRotationPitch);
    }
}
