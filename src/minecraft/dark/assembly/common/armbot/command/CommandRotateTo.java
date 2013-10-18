package dark.assembly.common.armbot.command;

import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.coding.IArmbot;
import dark.api.al.coding.IProgramableMachine;
import dark.api.al.coding.IProcessTask.TaskType;
import dark.api.al.coding.args.ArgumentIntData;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;
import dark.core.prefab.helpers.MathHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Rotates the armbot to a specific direction. If not specified, it will turn right.
 *
 * @author DarkGuardsman */
public class CommandRotateTo extends TaskArmbot
{
    int targetRotationYaw = 0, targetRotationPitch = 0, currentRotationYaw, currentRotationPitch;

    public CommandRotateTo()
    {
        super("RotateTo", TaskType.DEFINEDPROCESS);
        this.defautlArguments.add(new ArgumentIntData("yaw", 0, 360, 0));
        this.defautlArguments.add(new ArgumentIntData("pitch", 0, 360, 0));
    }

    public CommandRotateTo(int yaw, int pitch)
    {
        super("RotateTo", TaskType.DEFINEDPROCESS);
        this.defautlArguments.add(new ArgumentIntData("yaw", yaw, 360, 0));
        this.defautlArguments.add(new ArgumentIntData("pitch", pitch, 360, 0));
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine device)
    {
        super.onMethodCalled(world, location, device);

        this.targetRotationYaw = (int) MathHelper.clampAngleTo360(UnitHelper.tryToParseInt(this.getArg("yaw")));
        this.targetRotationPitch = (int) MathHelper.clampAngleTo360(UnitHelper.tryToParseInt(this.getArg("pitch")));

        return ProcessReturn.CONTINUE;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        super.onUpdate();

        this.armbot.moveArmTo(this.targetRotationYaw, this.targetRotationPitch);

        return Math.abs(this.armbot.getRotation().y - this.targetRotationPitch) > 0 && Math.abs(this.armbot.getRotation().x - this.targetRotationYaw) > 0 ? ProcessReturn.CONTINUE : ProcessReturn.DONE;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + Float.toString(this.targetRotationYaw) + " " + Float.toString(this.targetRotationPitch);
    }

    @Override
    public CommandRotateTo load(NBTTagCompound taskCompound)
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
    public TaskBase clone()
    {
        return new CommandRotateTo();
    }
}
