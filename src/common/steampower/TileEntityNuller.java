package steampower;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.extend.IElectricUnit;

public class TileEntityNuller extends TileEntityMachine implements IElectricUnit {
	@Override
	public float ampRequest()
	{
		return 200;
	}
	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}
	@Override
	public float getVoltage()
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
