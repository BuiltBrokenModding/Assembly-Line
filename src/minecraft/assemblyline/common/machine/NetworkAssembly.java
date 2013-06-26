package assemblyline.common.machine;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.core.vector.Vector3;

import net.minecraft.tileentity.TileEntity;
import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkTileEntities;

public class NetworkAssembly extends NetworkTileEntities
{
	/** List of network members that are providing power for the network */
	private List<TileEntity> powerSources = new ArrayList<TileEntity>();
	public final int powerRange = 20;

	public NetworkAssembly(INetworkPart... parts)
	{
		super(parts);
	}

	public boolean canRun(TileEntityAssembly tile)
	{
		if (tile != null && !tile.powered)
		{
			for (TileEntity entity : powerSources)
			{
				Vector3 start = new Vector3(tile);
				PowerPathFinder path = new PowerPathFinder(tile.worldObj, start, new Vector3(entity), powerRange);
				path.init(start);
				return path.results.size() > 0;
			}
		}
		return tile != null && tile.powered;
	}

	@Override
	public void postMergeProcessing(NetworkTileEntities network)
	{
		NetworkAssembly newNetwork = new NetworkAssembly();
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpMembers();
	}

	@Override
	public boolean addNetworkPart(INetworkPart part)
	{
		boolean added = super.addNetworkPart(part);
		if (added && part instanceof TileEntityAssembly)
		{
			if (((TileEntityAssembly) part).powered)
			{
				this.markAsPowerSource((TileEntity) part);
			}
		}
		return added;
	}

	@Override
	public boolean isValidMember(INetworkPart part)
	{
		return super.isValidMember(part) && part instanceof TileEntityAssembly;
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
