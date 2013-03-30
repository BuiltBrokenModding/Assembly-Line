package hydraulic.api;

import universalelectricity.core.block.IConnectionProvider;
import hydraulic.core.liquidNetwork.HydraulicNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

/**
 * A machine that acts as one with the liquid network using the networks pressure for some function
 * that doesn't change the over all network pressure. So pipes, gauges, tubes, buffers, decor
 * blocks.
 */
public interface IFluidNetworkPart extends IPipeConnection, IColorCoded, IConnectionProvider, ITankContainer
{
	/**
	 * gets the devices pressure from a given side for input
	 */
	public double getMaxPressure(ForgeDirection side);

	/**
	 * The max amount of liquid that can flow per request
	 */
	public int getMaxFlowRate(LiquidStack stack, ForgeDirection side);

	/**
	 * The Fluid network that this machine is part of
	 */
	public HydraulicNetwork getNetwork();

	/**
	 * sets the machines network
	 */
	public void setNetwork(HydraulicNetwork network);

	/**
	 * Called when the pressure on the machine reachs max
	 * 
	 * @param damageAllowed - can this tileEntity cause grief damage
	 * @return true if the device over pressured and destroyed itself
	 */
	public boolean onOverPressure(Boolean damageAllowed);

	/**
	 * size of the pipes liquid storage ability
	 */
	public int getTankSize();
	
	public ILiquidTank getTank();
	
	public void setTankContent(LiquidStack stack);

}
