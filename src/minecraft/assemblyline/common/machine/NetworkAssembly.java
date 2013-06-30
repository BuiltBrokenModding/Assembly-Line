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
	public final int MAX_POWER_RANGE = 20;

	public NetworkAssembly(INetworkPart... parts)
	{
		super(parts);
	}

	/** Detects if the tile can run by tracking down a tileEntity marked as a power provider in the
	 * network. Does use a pathfinder that will work threw at least a sphere with a radius the same
	 * as the max power range of the network
	 * 
	 * @param tile - tileEntity which is mainly used as a way to locate the tile and gets its world
	 * @return true if the tile can be powered by the network */
	public TileEntityAssembly canRun(TileEntityAssembly tile)
	{
		if (tile != null && !tile.powered && this.powerSources.size() > 0)
		{
			for (TileEntity entity : powerSources)
			{
				if (entity instanceof TileEntityAssembly && ((TileEntityAssembly) entity).powered)
				{
					Vector3 start = new Vector3(tile);
					Vector3 end = new Vector3(entity);
					if (start.distanceTo(end) <= this.MAX_POWER_RANGE)
					{
						PowerPathFinder path = new PowerPathFinder(tile.worldObj, start, end, MAX_POWER_RANGE);
						path.init(start);
						if (path.results.size() > 0)
						{
							return (TileEntityAssembly) entity;
						}
					}
				}
			}
		}
		return null;
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
