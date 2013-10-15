package dark.assembly.common.armbot.command;

import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbotTask.TaskType;

public class CommandDrop extends Command
{
    public CommandDrop()
    {
        super("drop", TaskType.DEFINEDPROCESS);
    }

    @Override
    public boolean onUpdate()
    {
        super.onUpdate();

        this.armbot.drop("all");
        this.worldObj.playSound(this.armbotPos.x, this.armbotPos.x, this.armbotPos.x, "random.pop", 0.2F, ((this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F, true);

        return false;
    }

    @Override
    public String toString()
    {
        return "DROP";
    }

    @Override
    public Command clone()
    {
        return new CommandDrop();
    }
}
