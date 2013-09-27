package dark.core.common.transmit;

import net.minecraftforge.common.Configuration;
import universalelectricity.compatibility.TileEntityUniversalConductor;
import dark.core.prefab.IExtraInfo.IExtraTileEntityInfo;

public class TileEntityWire extends TileEntityUniversalConductor implements IExtraTileEntityInfo
{
    int updateTick = 0;

    @Override
    public float getResistance()
    {
        return BlockWire.wireResistance;
    }

    @Override
    public float getCurrentCapacity()
    {
        return BlockWire.ampMax;
    }

    @Override
    public boolean hasExtraConfigs()
    {
        return false;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {

    }

}
