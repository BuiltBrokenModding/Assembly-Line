package dark.common.debug;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.compatibility.TileEntityUniversalElectrical;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.electricity.ElectricityPack;

public class TileEntityInfSupply extends TileEntityUniversalElectrical implements IElectrical
{

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            System.out.println("Inf power supply cycle " + this.ticks);
            this.produce();
        }
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return true;
    }

    @Override
    public ElectricityPack provideElectricity(ForgeDirection from, ElectricityPack request, boolean doProvide)
    {
        return this.canConnect(from) ? request : null;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        // TODO Auto-generated method stub
        return Integer.MAX_VALUE;
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

    @Override
    public float getEnergyStored()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }

}
