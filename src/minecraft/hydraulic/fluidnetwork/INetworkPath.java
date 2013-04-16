package hydraulic.fluidnetwork;

import net.minecraft.tileentity.TileEntity;

public interface INetworkPath
{

	/**
	 * Gets a list of all the connected TileEntities that this conductor is connected to. The
	 * array's length should be always the 6 adjacent wires.
	 * 
	 * @return
	 */
	public TileEntity[] getAdjacentConnections();

	/**
	 * Instantly refreshes all connected blocks around the conductor, recalculating the connected
	 * blocks.
	 */
	public void updateAdjacentConnections();
	
	/**
	 * The Fluid network that this machine is part of
	 */
	public HydraulicNetwork getNetwork();

	/**
	 * sets the machines network
	 */
	public void setNetwork(HydraulicNetwork network);
}
