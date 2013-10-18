package dark.assembly.common.armbot.command;

import dark.api.al.coding.IArmbot;
import dark.assembly.common.armbot.TaskBaseArmbot;
import dark.assembly.common.armbot.TaskBaseProcess;

public class CommandDrop extends TaskBaseArmbot
{
    public CommandDrop()
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
        return new CommandDrop();
    }

}
