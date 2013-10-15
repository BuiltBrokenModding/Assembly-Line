package dark.assembly.common.armbot.command;

import dark.api.al.armbot.Command;

public class CommandReturn extends Command
{
    public static final float IDLE_ROTATION_PITCH = 0;
    public static final float IDLE_ROTATION_YAW = 0;

    private CommandRotateTo rotateToCommand;

    @Override
    public void onStart()
    {
        this.rotateToCommand = (CommandRotateTo) this.commandManager.getNewCommand(this.tileEntity, CommandRotateTo.class, new String[] { "0", "0" });
        this.rotateToCommand.onStart();
    }

    @Override
    protected boolean onUpdate()
    {
        if (this.rotateToCommand == null)
        {
            this.onStart();
        }

        return this.rotateToCommand.onUpdate();
    }

    @Override
    public void onEnd()
    {
        this.rotateToCommand.onEnd();
    }

    @Override
    public String toString()
    {
        return "RETURN";
    }

}
