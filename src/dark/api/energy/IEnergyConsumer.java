package dark.api.energy;

import net.minecraftforge.common.ForgeDirection;

public interface IEnergyConsumer extends IEnergyDevice
{
    public float receiveEnergy(ForgeDirection from, EnergyPack receive, boolean doReceive);

    public EnergyPack getRequest(ForgeDirection direction);
}
