package dark.api.energy;

import net.minecraftforge.common.ForgeDirection;

/** Applied to objects that can accept energy. Normally TileEntities
 *
 * @author DarkGuardsman */
public interface IEnergyAcceptor extends IEnergyDevice
{
    /** Called when this machines receives energy
     *
     * @param from - direction/side the energy came from
     * @param pack - packet of energy
     * @param doReceive - true to do the task, false to simulate
     * @return the amount of energy accepted */
    public float receiveEnergy(ForgeDirection from, EnergyPack pack, boolean doReceive);

    /** Called to see how much energy this device needs on the given side */
    public EnergyPack getRequest(ForgeDirection direction, String type);

    /** Checked to see if this device can accept the packet from the direction. */
    public boolean canAccept(ForgeDirection direction, String type);
}
