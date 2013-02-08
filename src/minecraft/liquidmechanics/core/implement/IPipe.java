package liquidmechanics.core.implement;

import liquidmechanics.core.pressure.FluidPressureNetwork;
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
public interface IPipe
{
	/**
	 * The Fluid network that this pipe is part of
	 */
	public FluidPressureNetwork getNetwork();

	public void setNetwork(FluidPressureNetwork network);

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
	 * The maximum amount of amps this pipe can handle before bursting. This is calculating
	 * PER TICK!
	 * 
	 * @return The amount of amps in volts
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
