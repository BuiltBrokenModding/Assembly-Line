package hydraulic.api;

import hydraulic.core.liquidNetwork.HydraulicNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

/**
 * A machine that acts as one with the liquid network using the networks pressure for some function
 * that doesn't change the over all network pressure. So pipes, gauges, tubes, buffers, decor
 * blocks.
 */
public interface ILiquidNetworkPart
{
	/**
	 * gets the devices pressure from a given side for input
	 */
	public double getMaxPressure(ForgeDirection side);

	/**
	 * The Fluid network that this machine is part of
	 */
	public HydraulicNetwork getNetwork();

	/**
	 * sets the machines network
	 */
	public void setNetwork(HydraulicNetwork network);

	/**
	 * The tileEntities surrounding the block
	 * 
	 * @return
	 */
	public TileEntity[] getConnectedBlocks();

	/**
	 * The max amount of liquid that can flow per request
	 */
	public int getMaxFlowRate(LiquidStack stack, ForgeDirection side);

	/**
	 * Called when the pressure on the machine reachs max
	 * 
	 * @param damageAllowed - can this tileEntity cause grief damage
	 * @return true if the device over pressured and destroyed itself
	 */
	public boolean onOverPressure(Boolean damageAllowed);

	/**
	 * Resets the pipe and recalculate connection IDs again
	 */
	public void reset();

	/**
	 * Instantly refreshes all connected blocks
	 */
	public void refreshConnectedBlocks();

	/**
	 * Adds a connection between this machine and another machine
	 * 
	 * @param tileEntity - Must be either a producer, consumer or a conductor
	 * @param side - side in which the connection is coming from
	 */
	public void updateConnection(TileEntity tileEntity, ForgeDirection side);

	public void updateConnectionWithoutSplit(TileEntity connectorFromSide, ForgeDirection orientation);

}
