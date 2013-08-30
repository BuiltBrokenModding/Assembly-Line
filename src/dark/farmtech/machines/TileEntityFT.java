package dark.farmtech.machines;

import dark.farmtech.FarmTech;
import dark.prefab.TileEntityMachine;

/** Prefab class for all farm blocks to remove the need for some configuration of the super class
 *
 * @author Darkguardsman */
public abstract class TileEntityFT extends TileEntityMachine
{
    @Override
    public String getChannel()
    {
        return FarmTech.CHANNEL;
    }

}
