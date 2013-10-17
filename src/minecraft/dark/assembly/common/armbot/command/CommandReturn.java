package dark.assembly.common.armbot.command;

import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;

public class CommandReturn extends TaskArmbot
{
    public static final float IDLE_ROTATION_PITCH = 0;
    public static final float IDLE_ROTATION_YAW = 0;

    private CommandRotateTo rotateToCommand;

    public CommandReturn()
    {
        super("Return", TaskType.DEFINEDPROCESS);
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (this.rotateToCommand == null)
        {
            this.rotateToCommand = new CommandRotateTo(0, 0);
            this.rotateToCommand.onMethodCalled(this.worldObj, this.devicePos, armbot);
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

    @Override
    public TaskBase clone()
    {
        return new CommandReturn();
    }

}
