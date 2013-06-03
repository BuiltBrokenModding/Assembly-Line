package hydraulic.api;

import hydraulic.network.TileNetwork;
import net.minecraft.tileentity.TileEntity;

public interface INetworkPart extends ITileConnector
{
	/**
	 * Array of connections this tile has to other tiles
	 */
	public TileEntity[] getNetworkConnections();

	/**
	 * Update the connection this tile has to other tiles
	 */
	public void updateNetworkConnections();

	/**
	 * Gets the networkPart's primary network
	 */
	public TileNetwork getTileNetwork();

	/**
	 * Sets the networkPart's primary network
	 */
	public void setTileNetwork(TileNetwork fluidNetwok);
}
