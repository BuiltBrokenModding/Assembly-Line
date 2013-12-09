package dark.api.energy;

import net.minecraftforge.common.ForgeDirection;

/** Applied to objects that can provide energy
 * 
 * @author DarkGuardsman */
public interface IEnergyProvider extends IEnergyDevice
{
    /** Called to have the tile provide energy */
    public EnergyPack requestEnergy(ForgeDirection from, EnergyPack request, boolean doProvide);

    /** Gets the energy provided at the given time on the given side */
    public EnergyPack getEnergyOutput(ForgeDirection direction, String type);

    /** Can the device provide energy on the given side */
    public boolean canOutputEnergy(ForgeDirection side, String type);
}
