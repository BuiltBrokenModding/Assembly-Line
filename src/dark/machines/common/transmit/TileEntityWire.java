package dark.machines.common.transmit;

import universalelectricity.compatibility.TileEntityUniversalConductor;
import dark.api.ColorCode;

public class TileEntityWire extends TileEntityUniversalConductor
{
    protected int updateTick = 1;
    protected ColorCode color = ColorCode.BLACK;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            if (ticks % this.updateTick == 0)
            {
                this.updateTick = this.worldObj.rand.nextInt(5) * 40 + 20;
                this.refresh();
            }
        }
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

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
