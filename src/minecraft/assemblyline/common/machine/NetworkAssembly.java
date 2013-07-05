package assemblyline.common.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IConnectionProvider;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.electricity.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkPowerTiles;
import dark.core.tile.network.NetworkTileEntities;

public class NetworkAssembly extends NetworkPowerTiles
{
	/** List of network members that are providing power for the network */
	private List<TileEntity> powerSources = new ArrayList<TileEntity>();
	public double wattStored = 0.0;

	public NetworkAssembly(INetworkPart... parts)
	{
		super(parts);
	}

	/** Checks if the tile can run as well sucks up energy for the tile to run */
	public boolean canRun(TileEntityAssembly tile)
	{
		if (tile != null && this.wattStored >= tile.getRequest(ForgeDirection.UNKNOWN))
		{
			this.wattStored -= tile.getRequest(ForgeDirection.UNKNOWN);
			return true;
		}
		return false;
	}

	/** Gets the amount of power this network needs
	 * 
	 * @param total - true for total network, false for amount equal to each power connection */
	public double getRequest(boolean total)
	{
		double watt = 1;
		for (INetworkPart part : this.getNetworkMemebers())
		{
			if (part instanceof TileEntityAssembly)
			{
				watt += ((TileEntityAssembly) part).getRequest(ForgeDirection.UNKNOWN);
			}
		}
		if (!total)
		{
			return watt / this.powerSources.size();
		}
		return watt;
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
				this.markAsPowerSource((TileEntity) part, true);
			}
		}
		if (added)
		{
			this.doCalc();
		}
		return added;
	}

	@Override
	public boolean isValidMember(INetworkPart part)
	{
		return super.isValidMember(part) && part instanceof TileEntityAssembly;
	}

	public void doCalc()
	{

	}

	/** Marks a tile as the source of power for the network
	 * 
	 * @param powered true to add, false to remove */
	public void markAsPowerSource(TileEntity entity, boolean powered)
	{
		if (powered)
		{
			if (!this.powerSources.contains(entity))
			{
				this.powerSources.add(entity);
			}
		}
		else
		{
			this.powerSources.remove(entity);
		}
	}

}
