package dark.assembly.common.armbot.command;

import universalelectricity.core.vector.Vector3;
import dark.api.al.coding.ILogicDevice;
import dark.api.al.coding.IDeviceTask.ProcessReturn;
import dark.api.al.coding.IDeviceTask.TaskType;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Rotates the armbot to a specific direction. If not specified, it will turn right.
 *
 * @author Calclavia */
public class CommandRotateBy extends TaskArmbot
{

    double targetRotationYaw = 0;
    double targetRotationPitch = 0;
    double deltaPitch = 0, deltaYaw = 90;
    double totalTicks = 0f;

    private CommandRotateTo rotateToCommand;

    public CommandRotateBy()
    {
        super("RotateBy", TaskType.DEFINEDPROCESS);
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        super.onMethodCalled(world, location, armbot);

        this.ticks = 0;

        if (this.getArg(0) != null)
        {
            this.targetRotationYaw = this.tileEntity.rotationYaw + this.getFloatArg(0);
            this.deltaYaw = this.getFloatArg(0);
        }
        else
        {
            this.targetRotationYaw = this.tileEntity.rotationYaw + 90;
        }

        if (this.getArg(1) != null)
        {
            this.targetRotationPitch = this.tileEntity.rotationPitch + this.getFloatArg(1);
            this.deltaPitch = this.getFloatArg(1);
        }
        else
        {
            this.targetRotationPitch = this.armbot.getRotation().y;
        }

        float totalTicksYaw = Math.abs(this.targetRotationYaw - this.tileEntity.rotationYaw) / this.tileEntity.ROTATION_SPEED;
        float totalTicksPitch = Math.abs(this.targetRotationPitch - this.tileEntity.rotationPitch) / this.tileEntity.ROTATION_SPEED;
        this.totalTicks = Math.max(totalTicksYaw, totalTicksPitch);
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (this.rotateToCommand == null)
        {
            this.rotateToCommand = new CommandRotateTo();
            this.rotateToCommand.setParms(this.targetRotationYaw,this.targetRotationPitch);
            this.rotateToCommand.onMethodCalled(this.worldObj, this.armbotPos, armbot);
        }

        return this.rotateToCommand.onUpdate();
    }

    @Override
    public void loadProgress(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.targetRotationPitch = taskCompound.getFloat("rotPitch");
        this.targetRotationYaw = taskCompound.getFloat("rotYaw");
    }

    @Override
    public void saveProgress(NBTTagCompound taskCompound)
    {
        super.saveProgress(taskCompound);
        taskCompound.setFloat("rotPitch", this.targetRotationPitch);
        taskCompound.setFloat("rotYaw", this.targetRotationYaw);
    }

    @Override
    public String toString()
    {
        return "ROTATE " + Float.toString(this.deltaYaw) + " " + Float.toString(this.deltaPitch);
    }
}
