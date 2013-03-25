package hydraulic.api;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

public interface IPsiCreator extends IPipeConnector
{
	/**
	 * gets the PressureOutput of a device
	 */
	public int getPressureOut(LiquidStack stack, ForgeDirection dir);

	/**
	 * Quick way to check if the TE will output pressure
	 */
	public boolean getCanPressureTo(LiquidStack stack, ForgeDirection dir);
}
