package dark.assembly.common.armbot.command;


/** Used by arms to break a specific block in a position.
 *
 * @author DarkGuardsman */
public class CommandHarvest extends CommandBreak
{

    public CommandHarvest()
    {
        super("Harvest");
    }

    @Override
    public ProcessReturn onMethodCalled()
    {
        this.keep = true;
        return super.onMethodCalled();
    }
}
