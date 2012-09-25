package steampower;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.implement.IElectricityReceiver;

public class TileEntityNuller extends TileEntityMachine implements IElectricityReceiver {
	
	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}
	public double wattRequest()
	{
		return 400;
	}
	@Override
	public double getVoltage()
	{
		return 1000;
	}
	@Override
	public int getTickInterval()
	{
		return 20;
	}
	@Override
	public boolean canConnect(ForgeDirection side)
	{
		return true;
	}
}
