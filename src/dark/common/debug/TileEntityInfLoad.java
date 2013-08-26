package dark.common.debug;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.electricity.ElectricityPack;

public class TileEntityInfLoad extends TileEntity implements IElectrical
{

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        //TODO add wrench settings to close sides for testing
        return true;
    }

    @Override
    public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
    {
        if(receive != null)
        {
            System.out.println("Burning off "+receive.getWatts()+" watts of energy");
        }
        return this.canConnect(from) && receive != null ? receive.getWatts() : 0;
    }

    @Override
    public ElectricityPack provideElectricity(ForgeDirection from, ElectricityPack request, boolean doProvide)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        //TODO add config options to change this for testing
        return Integer.MAX_VALUE;
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

}
