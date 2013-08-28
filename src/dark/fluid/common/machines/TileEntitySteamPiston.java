package dark.fluid.common.machines;

import dark.core.blocks.TileEntityMachine;
import dark.fluid.common.FluidMech;

public class TileEntitySteamPiston extends TileEntityMachine
{

    @Override
    public String getChannel()
    {
        return FluidMech.CHANNEL;
    }
}
