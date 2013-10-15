package dark.assembly.common.armbot.command;

import dark.api.al.armbot.Command;

public class CommandDrop extends Command
{
    @Override
    protected boolean onUpdate()
    {
        super.onUpdate();

        this.tileEntity.drop("all");
        this.worldObj.playSound(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, "random.pop", 0.2F, ((this.tileEntity.worldObj.rand.nextFloat() - this.tileEntity.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F, true);

        return false;
    }

    @Override
    public String toString()
    {
        return "DROP";
    }
}
