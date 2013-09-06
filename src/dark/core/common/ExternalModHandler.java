package dark.core.common;

import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import dark.core.prefab.helpers.Pair;

/** Handles working with other mod without or without the need of the APIs.
 *
 * @author DarkGuardsman */
public class ExternalModHandler
{
    private static HashMap<String, Pair<Integer, Integer>> pipeMap = new HashMap<String, Pair<Integer, Integer>>();

    public static void mapBuildCraftPipes()
    {
        //TODO map pipe blockIDs, and metadata for later use
    }

    /** Is the tileEntity an instanceof IPipeTile and of type fluid from BuildCraft */
    public boolean isBCFluidPipe(TileEntity entity)
    {
        return entity instanceof IPipeTile && ((IPipeTile) entity).getPipeType() == PipeType.FLUID;
    }

    public boolean isBCPowerPipe(TileEntity entity)
    {
        return entity instanceof IPipeTile && ((IPipeTile) entity).getPipeType() == PipeType.POWER;
    }
}
