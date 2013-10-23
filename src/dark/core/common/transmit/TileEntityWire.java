package dark.core.common.transmit;

import universalelectricity.compatibility.TileEntityUniversalConductor;
import dark.api.ColorCode;

public class TileEntityWire extends TileEntityUniversalConductor
{
    protected int updateTick = 0;
    protected ColorCode color = ColorCode.BLACK;

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

}
