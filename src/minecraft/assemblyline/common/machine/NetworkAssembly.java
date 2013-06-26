package assemblyline.common.machine;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkTileEntities;

public class NetworkAssembly extends NetworkTileEntities
{
	/** List of network members that are providing power for the network */
	private List<TileEntity> powerSources = new ArrayList<TileEntity>();

	public NetworkAssembly(INetworkPart... parts)
	{
		super(parts);
	}

	public boolean canRun(TileEntityAssembly tile)
	{
		return false;
	}

	@Override
	public void postMergeProcessing(NetworkTileEntities network)
	{
		NetworkAssembly newNetwork = new NetworkAssembly();
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpConductors();
	}

	public boolean addNetworkPart(INetworkPart part)
	{
		boolean added = super.addNetworkPart(part);
		if (added)
		{

		}
		return added;
	}

	/** Marks a tile as the source of power for the network */
	public void markAsPowerSource(TileEntity entity)
	{
		if (!this.powerSources.contains(entity))
		{
			this.powerSources.add(entity);
		}
	}

	/** unmarks or removes the tile as a source of power for the network */
	public void removeAsPowerSource(TileEntity entity)
	{
		this.powerSources.remove(entity);
	}
}
