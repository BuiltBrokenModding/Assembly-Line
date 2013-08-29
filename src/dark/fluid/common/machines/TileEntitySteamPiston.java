package dark.fluid.common.machines;

import dark.fluid.common.FluidMech;
import dark.prefab.TileEntityMachine;

public class TileEntitySteamPiston extends TileEntityMachine
{

    @Override
    public String getChannel()
    {
        return FluidMech.CHANNEL;
    }
}
