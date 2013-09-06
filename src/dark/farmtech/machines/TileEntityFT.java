package dark.farmtech.machines;

import dark.core.prefab.TileEntityMachine;
import dark.farmtech.FarmTech;

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
