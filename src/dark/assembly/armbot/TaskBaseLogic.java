package dark.assembly.armbot;

import dark.api.al.coding.ILogicTask;

/** @author DarkGuardsman */
public abstract class TaskBaseLogic extends TaskBase implements ILogicTask
{
    public TaskBaseLogic(String name)
    {
        super(name, TaskType.DECISION);
    }
}
