package dark.assembly.common.armbot.command;

import com.builtbroken.common.science.units.UnitHelper;

import universalelectricity.core.vector.Vector3;
import dark.api.al.coding.IProgramableMachine;
import dark.api.al.coding.IProcessTask.ProcessReturn;
import dark.api.al.coding.IProcessTask.TaskType;
import dark.api.al.coding.args.ArgumentIntData;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;
import dark.core.prefab.helpers.MathHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Rotates an armbot by a set amount
 *
 * @author DarkGuardsman */
public class CommandRotateBy extends TaskArmbot
{

    int targetRotationYaw = 0, targetRotationPitch = 0, deltaPitch = 0, deltaYaw = 0;

    private CommandRotateTo rotateToCommand;

    public CommandRotateBy()
    {
        super("RotateBy", TaskType.DEFINEDPROCESS);
        this.defautlArguments.add(new ArgumentIntData("yaw", 0, 360, 0));
        this.defautlArguments.add(new ArgumentIntData("pitch", 0, 360, 0));
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        super.onMethodCalled(world, location, armbot);

        this.targetRotationYaw = (int) MathHelper.clampAngleTo360((float) (this.armbot.getRotation().x + UnitHelper.tryToParseInt(this.getArg("yaw"))));
        this.targetRotationYaw = (int) MathHelper.clampAngleTo360((float) (this.armbot.getRotation().x + UnitHelper.tryToParseInt(this.getArg("pitch"))));

        return ProcessReturn.CONTINUE;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (this.rotateToCommand == null)
        {
            this.rotateToCommand = new CommandRotateTo(this.targetRotationYaw, this.targetRotationPitch);
            this.rotateToCommand.onMethodCalled(this.worldObj, this.devicePos, armbot);
        }

        return this.rotateToCommand.onUpdate();
    }

    @Override
    public CommandRotateBy load(NBTTagCompound taskCompound)
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
    public String toString()
    {
        return super.toString() + " " + Float.toString(this.deltaYaw) + " " + Float.toString(this.deltaPitch);
    }

    @Override
    public TaskBase clone()
    {
        return new CommandRotateBy();
    }
}
