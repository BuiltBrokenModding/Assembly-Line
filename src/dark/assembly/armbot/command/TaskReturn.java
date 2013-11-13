package dark.assembly.armbot.command;

import dark.assembly.armbot.TaskBaseArmbot;
import dark.assembly.armbot.TaskBaseProcess;

public class TaskReturn extends TaskBaseArmbot
{
    public static final float IDLE_ROTATION_PITCH = 0;
    public static final float IDLE_ROTATION_YAW = 0;

    private TaskRotateTo rotateToCommand;

    public TaskReturn()
    {
        super("Return");
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (this.rotateToCommand == null)
        {
            this.rotateToCommand = new TaskRotateTo(0, 0);
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
        return new TaskReturn();
    }

}
