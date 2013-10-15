package dark.assembly.common.armbot.command;

import dark.assembly.common.armbot.Command;
import net.minecraft.nbt.NBTTagCompound;

public class CommandIdle extends Command
{
    /** The amount of time in which the machine will idle. */
    public int idleTime = 80;
    private int totalIdleTime = 80;

    @Override
    public void onStart()
    {
        super.onStart();

        if (this.getIntArg(0) > 0)
        {
            this.idleTime = this.getIntArg(0);
            this.totalIdleTime = this.idleTime;
        }
    }

    @Override
    protected boolean onUpdate()
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
    public void readFromNBT(NBTTagCompound taskCompound)
    {
        super.readFromNBT(taskCompound);
        this.idleTime = taskCompound.getInteger("idleTime");
        this.totalIdleTime = taskCompound.getInteger("idleTotal");
    }

    @Override
    public void writeToNBT(NBTTagCompound taskCompound)
    {
        super.writeToNBT(taskCompound);
        taskCompound.setInteger("idleTime", this.idleTime);
        taskCompound.setInteger("idleTotal", this.totalIdleTime);
    }

    @Override
    public String toString()
    {
        return "IDLE " + Integer.toString(this.totalIdleTime);
    }

}
