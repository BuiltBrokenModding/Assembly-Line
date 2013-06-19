package dark.core.api;

import net.minecraftforge.common.ForgeDirection;

public interface IHeatProducer
{
	/**
	 *Checks too see if this can produce heat
	 */
	public boolean getCanProduceHeat(ForgeDirection dir);

	/**
	 * Gets the amount of heat in joules this can output
	 */
	public double getHeatAmmount(ForgeDirection dir);
}
