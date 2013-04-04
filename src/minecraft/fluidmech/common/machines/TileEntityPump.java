package fluidmech.common.machines;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;

public class TileEntityPump extends TileEntityElectricityRunnable
{
	public static final double WATTS_PER_TICK = 100;
	private ForgeDirection outputSide = ForgeDirection.UNKNOWN;
	private ForgeDirection inputSide = ForgeDirection.UNKNOWN;
	
	
	@Override
	public ElectricityPack getRequest()
	{
		return new ElectricityPack(WATTS_PER_TICK / this.getVoltage(), this.getVoltage());
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction != outputSide && direction != inputSide ;
	}

}
