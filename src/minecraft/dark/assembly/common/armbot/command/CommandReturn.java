package dark.assembly.common.armbot.command;

import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbotTask.TaskType;

public class CommandReturn extends Command
{
    public static final float IDLE_ROTATION_PITCH = 0;
    public static final float IDLE_ROTATION_YAW = 0;

    private CommandRotateTo rotateToCommand;

    public CommandReturn()
    {
        super("Return", TaskType.DEFINEDPROCESS);
    }

    @Override
    public boolean onUpdate()
    {
        if (this.rotateToCommand == null)
        {
            this.rotateToCommand = (CommandRotateTo) this.commandManager.getNewCommand(this.tileEntity, CommandRotateTo.class, new String[] { "0", "0" });
            this.rotateToCommand.onStart();
        }

        return this.rotateToCommand.onUpdate();
    }

    @Override
    public void terminated()
    {
        this.rotateToCommand.terminated();
    }

    @Override
    public String toString()
    {
        return "RETURN";
    }

}
