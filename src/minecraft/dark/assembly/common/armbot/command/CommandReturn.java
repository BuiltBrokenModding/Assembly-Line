package dark.assembly.common.armbot.command;

import dark.assembly.common.armbot.TaskBaseArmbot;
import dark.assembly.common.armbot.TaskBaseProcess;

public class CommandReturn extends TaskBaseArmbot
{
    public static final float IDLE_ROTATION_PITCH = 0;
    public static final float IDLE_ROTATION_YAW = 0;

    private CommandRotateTo rotateToCommand;

    public CommandReturn()
    {
        super("Return");
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (this.rotateToCommand == null)
        {
            this.rotateToCommand = new CommandRotateTo(0, 0);
            this.rotateToCommand.onMethodCalled();
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
    public TaskBaseProcess clone()
    {
        return new CommandReturn();
    }

}
