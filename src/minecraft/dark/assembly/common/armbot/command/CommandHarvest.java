package dark.assembly.common.armbot.command;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import dark.api.al.armbot.IArmbot;

/** Used by arms to break a specific block in a position.
 *
 * @author Calclavia */
public class CommandHarvest extends CommandBreak
{
    private CommandRotateTo rotateToCommand;

    public CommandHarvest()
    {
        super("Harvest");
    }

    @Override
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot, Object[] arguments)
    {
        this.keep = true;
        return super.onMethodCalled(world, location, armbot, arguments);
    }
}
