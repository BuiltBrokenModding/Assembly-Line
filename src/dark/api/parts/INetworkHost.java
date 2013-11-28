package dark.api.parts;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

public interface INetworkHost extends ITileConnector
{
    /** Array of connections this tile has to other tiles */
    public List<TileEntity> getNetworkConnections();

    /** Update the connection this tile has to other tiles */
    public void refresh();

    /** Gets all networks this machine is part of */
    public List<NetworkTileEntities> getTileNetworks();

    /** Adds a network to this machine */
    public void addNetwork(NetworkTileEntities network);

    /** Removes a network from this machine */
    public void removeNetwork(NetworkTileEntities network);
}
