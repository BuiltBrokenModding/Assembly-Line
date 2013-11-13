package dark.assembly.armbot.command;

import dark.api.al.coding.IArmbot;
import dark.assembly.armbot.TaskBaseArmbot;
import dark.assembly.armbot.TaskBaseProcess;

public class TaskDrop extends TaskBaseArmbot
{
    public TaskDrop()
    {
        super("drop");
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (super.onUpdate() == ProcessReturn.CONTINUE)
        {
            ((IArmbot) this.program.getMachine()).drop("all");
        }
        return ProcessReturn.DONE;
    }

    @Override
    public TaskBaseProcess clone()
    {
        return new TaskDrop();
    }

}
