package dark.assembly.common.armbot.command;

import net.minecraft.nbt.NBTTagCompound;
import dark.api.al.coding.IDeviceTask;
import dark.api.al.coding.IDeviceTask.TaskType;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;

public class CommandDrop extends TaskArmbot
{
    public CommandDrop()
    {
        super("drop", TaskType.DEFINEDPROCESS);
    }

    @Override
    public ProcessReturn onUpdate()
    {
        super.onUpdate();

        this.armbot.drop("all");
        this.worldObj.playSound(this.devicePos.x, this.devicePos.x, this.devicePos.x, "random.pop", 0.2F, ((this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F, true);

        return ProcessReturn.DONE;
    }

    @Override
    public String toString()
    {
        return "DROP";
    }

    @Override
    public TaskBase clone()
    {
        return new CommandDrop();
    }


}
