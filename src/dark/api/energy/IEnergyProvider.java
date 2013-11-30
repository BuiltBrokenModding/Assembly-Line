package dark.api.energy;

import net.minecraftforge.common.ForgeDirection;

public interface IEnergyProvider extends IEnergyDevice
{
    public EnergyPack provideEnergy(ForgeDirection from, EnergyPack request, boolean doProvide);

    public EnergyPack getEnergyProduce(ForgeDirection direction);
}
