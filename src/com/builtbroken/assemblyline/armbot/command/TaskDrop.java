package com.builtbroken.assemblyline.armbot.command;

import com.builtbroken.assemblyline.api.coding.IArmbot;
import com.builtbroken.assemblyline.armbot.TaskBaseArmbot;
import com.builtbroken.assemblyline.armbot.TaskBaseProcess;

import universalelectricity.core.vector.Vector2;

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
