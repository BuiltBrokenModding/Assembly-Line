package dark.assembly.armbot.command;

import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.IArmbot;
import dark.assembly.armbot.TaskBaseArmbot;
import dark.assembly.armbot.TaskBaseProcess;

public class TaskDrop extends TaskBaseArmbot
{
    public TaskDrop()
    {
        super("drop");
        this.UV = new Vector2(20, 80);
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
