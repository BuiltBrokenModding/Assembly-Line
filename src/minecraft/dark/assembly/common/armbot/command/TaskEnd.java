package dark.assembly.common.armbot.command;

import dark.assembly.common.armbot.TaskBase;

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
