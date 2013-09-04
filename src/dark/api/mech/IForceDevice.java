package dark.api.mech;

import dark.api.parts.ITileConnector;
import net.minecraftforge.common.ForgeDirection;

/** Think of this in the same way as an electrical device from UE. getforce methods are designed to
 * get the idea amount of foce that a side should be outputing at the time. Apply force is the input
 * for force for the tile, and should return the actually force the machine is using. Supply is when
 * the code asks for your tile to output force on the side, just return the force value don't try to
 * apply the force to other machines.
 *
 * Tip Supply should never equal load as everything will stop moving since the load equals the
 * amount of force. The supply of force should be greater
 *
 * @author DarkGuardsman */
public interface IForceDevice extends ITileConnector
{
    /** Applies force to this tile
     *
     * @param side - side its coming from
     * @param force - amount of force
     * @return amount of force actually loaded down */
    public float applyForce(ForgeDirection side, float force);

    /** @param side - side that force should be supplied in
     * @return force to apply in direction */
    public float supplyForce(ForgeDirection side);

    /** Idea force to output on the side */
    public float getForceOut(ForgeDirection side);

    /** Idea force to load down on the side */
    public float getForceLoad(ForgeDirection side);
}
