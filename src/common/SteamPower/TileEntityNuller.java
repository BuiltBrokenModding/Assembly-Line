package SteamPower;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.extend.IElectricUnit;

public class TileEntityNuller extends TileEntityMachine implements IElectricUnit {

	public float electricityRequest()
	{
		return 100;
	}
	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}
	public float getVoltage()
	{
		return 1000;
	}
	public int getTickInterval()
	{
		return 1;
	}
	public boolean canConnect(ForgeDirection side)
	{
		return true;
	}
}
