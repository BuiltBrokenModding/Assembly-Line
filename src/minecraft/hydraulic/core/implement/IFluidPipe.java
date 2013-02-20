package hydraulic.core.implement;

import hydraulic.core.liquids.HydraulicNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.electricity.ElectricityNetwork;

/**
 * Must be applied to all tile entities that are conductors.
 * 
 * @author Calclavia
 * 
 */
public interface IFluidPipe extends IPsiMachine
{
	/**
	 * The Fluid network that this pipe is part of
	 */
	public HydraulicNetwork getNetwork();

	public void setNetwork(HydraulicNetwork network);

	/**
	 * The UE tile entities that this conductor is connected to.
	 * 
	 * @return
	 */
	public TileEntity[] getConnectedBlocks();

	/**
	 * Gets the resistance the pipes too the liquid going threw it
	 * 
	 * @return The amount of Ohm's of resistance.
	 */
	public double getResistance(LiquidStack stack);

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
