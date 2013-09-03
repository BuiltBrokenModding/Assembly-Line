package dark.core.common.debug;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.compatibility.TileEntityUniversalElectrical;
import universalelectricity.core.electricity.ElectricityPack;

public class TileEntityInfLoad extends TileEntityUniversalElectrical
{

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 1000 == 0)
            {
                this.setEnergyStored(0);
            }
        }
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        //TODO add wrench settings to close sides for testing
        return true;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        //TODO add config options to change this for testing
        return 10000;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float getVoltage()
    {
        return 120;
    }

    @Override
    public float getMaxEnergyStored()
    {
        return Integer.MAX_VALUE;
    }

}
