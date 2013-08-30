package dark.farmtech.machines;

import dark.farmtech.FarmTech;
import dark.prefab.TileEntityMachine;

public abstract class TileEntityFT extends TileEntityMachine
{
    @Override
    public String getChannel()
    {
        return FarmTech.CHANNEL;
    }

}
