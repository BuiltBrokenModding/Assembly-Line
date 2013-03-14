package hydraulic.api;

import hydraulic.core.liquidNetwork.HydraulicNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

/**
 * Must be applied to all tile entities that are conductors.
 * 
 * @author Calclavia
 * 
 */
public interface IFluidPipe extends IColorCoded
{
	/**
	 * The Fluid network that this pipe is part of
	 */
	public HydraulicNetwork getNetwork();

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
	public int getMaxFlowRate(LiquidStack stack);

	/**
	 * gets the pipe's max pressure before bursting
	 */
	public double getMaxPressure();

	/**
	 * Called when the pressure on the pipe passes max
	 */
	public void onOverPressure();

	/**
	 * Resets the pipe and recalculate connection IDs again
	 */
	public void reset();

	/**
	 * Instantly refreshes all connected blocks
	 */
	public void refreshConnectedBlocks();

	/**
	 * Adds a connection between this conductor and a UE unit
	 * 
	 * @param tileEntity - Must be either a producer, consumer or a conductor
	 * @param side - side in which the connection is coming from
	 */
	public void updateConnection(TileEntity tileEntity, ForgeDirection side);

	public void updateConnectionWithoutSplit(TileEntity connectorFromSide, ForgeDirection orientation);
}
