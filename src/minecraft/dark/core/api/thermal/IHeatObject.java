package dark.core.api.thermal;

import net.minecraftforge.common.ForgeDirection;

/**
 * Used by TileEntities or Entities to show heat stored and cooling rate of the object
 * 
 * @author DarkGuardsman
 * 
 */
public interface IHeatObject
{
	/**
	 * Amount of heat stored in the body of the object
	 * 
	 * @return amount of heat in generic units
	 */
	public double getHeat(ForgeDirection side);

	/**
	 * Sets the heat level of the object or increase it
	 * 
	 * @param amount - amount to set or increase by
	 * @param incrase - true if should increase the current heat level
	 */
	public void setHeat(double amount, boolean incrase);

	/**
	 * Rate by which this object can cool by from the given side
	 */
	public double getCoolingRate(ForgeDirection side);
}
