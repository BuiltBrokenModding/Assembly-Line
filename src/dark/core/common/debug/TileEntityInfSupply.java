package dark.core.common.debug;

import java.util.EnumSet;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.compatibility.TileEntityUniversalElectrical;

public class TileEntityInfSupply extends TileEntityUniversalElectrical
{

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            //System.out.println("Inf power supply cycle " + this.ticks);
            this.produce();
            if (this.ticks % 10 == 0)
            {
                this.setEnergyStored(this.getEnergyStored() + (this.getProvide(ForgeDirection.UNKNOWN) * 10));
            }
        }
    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return true;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        // TODO Auto-generated method stub
        return 1000;
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
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }

}
