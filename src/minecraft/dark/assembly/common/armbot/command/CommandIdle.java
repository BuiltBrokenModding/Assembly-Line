package dark.assembly.common.armbot.command;

import com.builtbroken.common.science.units.UnitHelper;

import universalelectricity.core.vector.Vector3;
import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class CommandIdle extends Command
{

    /** The amount of time in which the machine will idle. */
    public int idleTime = 80;
    private int totalIdleTime = 80;

    public CommandIdle()
    {
        super("wait");
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot, Object[] arguments)
    {
        super.onMethodCalled(world, location, armbot, arguments);

        if (UnitHelper.tryToParseInt("" + this.getArg(0)) > 0)
        {
            this.idleTime = UnitHelper.tryToParseInt("" + this.getArg(0));
            this.totalIdleTime = this.idleTime;
            return true;
        }
        return false;
    }

    @Override
    public boolean onUpdate()
    {
        /** Randomly move the arm to simulate life in the arm if the arm is powered */
        // this.tileEntity.rotationPitch *= 0.98 * this.world.rand.nextFloat();

        if (this.idleTime > 0)
        {
            this.idleTime--;
            return true;
        }

        return false;
    }

    @Override
    public Command readFromNBT(NBTTagCompound taskCompound)
    {
        super.readFromNBT(taskCompound);
        this.idleTime = taskCompound.getInteger("idleTime");
        this.totalIdleTime = taskCompound.getInteger("idleTotal");
        return this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound taskCompound)
    {
        super.writeToNBT(taskCompound);
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
    public Command clone()
    {
        return new CommandIdle();
    }

}
