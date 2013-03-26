package hydraulic.api;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

public interface IPsiCreator extends IPipeConnection
{
	/**
	 * gets the pressure produced from that side of the machine. Use canConnect method to allow a
	 * pipe to connect to the side first.
	 * 
	 * @param stack - liquid stack that the pressure is being requested for
	 * @param dir - side being pressured
	 * @return - amount of pressure produced
	 */
	public int getPressureOut(LiquidStack stack, ForgeDirection dir);

}
