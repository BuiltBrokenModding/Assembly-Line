package hydraulic.api;

import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public interface INetworkFluidPart extends IColorCoded, ITankContainer, INetworkPart
{
	/**
	 * Gets the part's main tank for shared storage
	 */
	public LiquidTank getTank();

	/**
	 * Sets the content of the part's main tank
	 */
	public void setTankContent(LiquidStack stack);
}
