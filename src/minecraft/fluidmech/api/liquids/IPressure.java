package fluidmech.api.liquids;

import net.minecraftforge.common.ForgeDirection;

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
}
