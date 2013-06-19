package dark.core.api;

import net.minecraft.tileentity.TileEntity;
import dark.core.tile.network.NetworkTileEntities;

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
	public NetworkTileEntities getTileNetwork();

	/**
	 * Sets the networkPart's primary network
	 */
	public void setTileNetwork(NetworkTileEntities fluidNetwok);
}
