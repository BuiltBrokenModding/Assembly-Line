package dark.assembly.armbot.command;

import dark.assembly.armbot.TaskBase;

/** @author DarkGuardsman */
public class TaskEnd extends TaskBase
{
    public TaskEnd()
    {
        super("end", TaskType.END);
    }

    @Override
    public TaskBase clone()
    {
        return new TaskEnd();
    }
}
