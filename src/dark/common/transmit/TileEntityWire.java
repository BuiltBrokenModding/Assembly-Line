package dark.common.transmit;

import universalelectricity.compatibility.TileEntityUniversalConductor;

public class TileEntityWire extends TileEntityUniversalConductor
{

    @Override
    public float getResistance()
    {
        return 0.001f;
    }

    @Override
    public float getCurrentCapacity()
    {
        return 10000f;
    }

}
