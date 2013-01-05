package liquidmechanics.api;

import liquidmechanics.common.handlers.LiquidData;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;

public interface IPressure
{
	/**
	 * @param type - Liquid type
	 * @param dir - direction pressure is being request to output
	 * @return pressure if can output for the type or direction
	 */
	public int presureOutput(LiquidData type, ForgeDirection dir);

	/**
	 * Quick way to check if the TE will output pressure
	 * 
	 * @param type - Liquid type
	 * @param dir - direction
	 * @return
	 */
	public boolean canPressureToo(LiquidData type, ForgeDirection dir);
	/**
	 * gets the LiquidData linked to the TE
	 */
	public LiquidData getLiquidType();
}
