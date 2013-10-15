package dark.assembly.common.armbot.command;

import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbot;
import dark.core.prefab.helpers.MathHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Rotates the armbot to a specific direction. If not specified, it will turn right.
 *
 * @author DarkGuardsman */
public class CommandRotateTo extends Command
{
    public CommandRotateTo()
    {
        super("RotateTo");
    }

    float targetRotationYaw = 0, targetRotationPitch = 0, currentRotationYaw, currentRotationPitch;

    @Override
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot, Object[] arguments)
    {
        super.onMethodCalled(world, location, armbot, arguments);

        if (this.getArg(0) != null)
        {
            this.targetRotationYaw = UnitHelper.tryToParseFloat("" + this.getArg(0));
        }

        if (this.getArg(1) != null)
        {
            this.targetRotationPitch = UnitHelper.tryToParseFloat("" + this.getArg(1));
        }

        MathHelper.clampAngleTo360(this.targetRotationPitch);
        MathHelper.clampAngleTo360(this.targetRotationYaw);

        return true;
    }

    @Override
    public boolean onUpdate()
    {
        super.onUpdate();

        this.currentRotationYaw = (float) this.armbot.getRotation().x;
        this.currentRotationPitch = (float) this.armbot.getRotation().y;
        this.armbot.moveArmTo(this.targetRotationYaw, this.targetRotationPitch);

        return Math.abs(this.currentRotationPitch - this.targetRotationPitch) > 0.01f && Math.abs(this.currentRotationYaw - this.targetRotationYaw) > 0.01f;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + Float.toString(this.targetRotationYaw) + " " + Float.toString(this.targetRotationPitch);
    }

    @Override
    public Command readFromNBT(NBTTagCompound taskCompound)
    {
        super.readFromNBT(taskCompound);
        this.targetRotationPitch = taskCompound.getFloat("rotPitch");
        this.targetRotationYaw = taskCompound.getFloat("rotYaw");
        return this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound taskCompound)
    {
        super.writeToNBT(taskCompound);
        taskCompound.setFloat("rotPitch", this.targetRotationPitch);
        taskCompound.setFloat("rotYaw", this.targetRotationYaw);
        return taskCompound;
    }

    @Override
    public Command clone()
    {
        return new CommandRotateTo();
    }
}
