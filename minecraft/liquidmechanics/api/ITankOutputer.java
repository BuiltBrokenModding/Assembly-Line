package liquidmechanics.api;

import liquidmechanics.api.helpers.LiquidHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;

public interface ITankOutputer extends ITankContainer
{
	/**
	 * @param type - Liquid type
	 * @param dir - direction pressure is being request to output
	 * @return pressure if can output for the type or direction
	 */
	public int presureOutput(LiquidHelper type, ForgeDirection dir);

	/**
	 * Quick way to check if the TE will output pressure
	 * 
	 * @param type - Liquid type
	 * @param dir - direction
	 * @return
	 */
	public boolean canPressureToo(LiquidHelper type, ForgeDirection dir);
}
