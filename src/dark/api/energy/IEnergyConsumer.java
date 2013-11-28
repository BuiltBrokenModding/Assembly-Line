package dark.api.energy;

import net.minecraftforge.common.ForgeDirection;

public interface IEnergyConsumer extends IEnergyDevice
{
    public float receiveEnergy(ForgeDirection from, EnergyPacket receive, boolean doReceive);

    public EnergyPacket getRequest(ForgeDirection direction);
}
