package dark.common.transmit;

import universalelectricity.compatibility.TileEntityUniversalConductor;

public class TileEntityWire extends TileEntityUniversalConductor
{
    int updateTick = 0;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (this.ticks % 1 + updateTick == 0)
        {
            this.updateTick = this.worldObj.rand.nextInt(200);
            this.refresh();
        }
    }

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

    @Override
    public boolean canUpdate()
    {
        return true;
    }

}
