package dark.api.energy;

import net.minecraftforge.common.ForgeDirection;

public interface IEnergyProvider extends IEnergyDevice
{
    public EnergyPacket provideEnergy(ForgeDirection from, EnergyPacket request, boolean doProvide);

    public EnergyPacket getEnergyProduce(ForgeDirection direction);
}
