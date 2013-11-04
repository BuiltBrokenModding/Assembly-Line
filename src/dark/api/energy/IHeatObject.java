package dark.api.energy;

import net.minecraftforge.common.ForgeDirection;
import dark.api.parts.ITileConnector;

/** Used by TileEntities or Entities to show heat stored and cooling rate of the object
 *
 * @author DarkGuardsman */
public interface IHeatObject extends ITileConnector
{

    /** Amount of heat stored in the body of the object. Think of it as a battery for heat but
     * remember that heat should be lost rather than keep over time.
     *
     * @return amount of heat in generic units */
    public float getHeat(ForgeDirection side);

    /** Sets the heat level of the object or increases it
     *
     * @param amount - amount to set or increase by. Can be neg to indicate a lose of heat
     * @param incrase - true if should increase the current heat level */
    public void setHeat(double amount, boolean incrase);

    /** Rate by which this object can cool by from the given side. Generally is decided by the blocks
     * next to it and the biome the block is in. Is a self heat loss value. */
    public float getCoolingRate(ForgeDirection side);
}
