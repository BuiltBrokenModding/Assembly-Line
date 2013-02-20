package hydraulic.core.implement;

import hydraulic.core.liquids.LiquidData;
import net.minecraftforge.common.ForgeDirection;

public interface IPsiCreator
{
	/**
	 * gets the PressureOutput of a device 
	 */
	public int getPressureOut(LiquidData type, ForgeDirection dir);

	/**
	 * Quick way to check if the TE will output pressure
	 */
	public boolean getCanPressureTo(LiquidData type, ForgeDirection dir);
}
