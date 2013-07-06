package assemblyline.common.machine;

import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkPowerTiles;
import dark.core.tile.network.NetworkTileEntities;

public class NetworkAssembly extends NetworkPowerTiles
{

	/** Power stored to be used by network members */
	private double wattStored = 0.0;

	public NetworkAssembly(INetworkPart... parts)
	{
		super(parts);
	}

	public NetworkTileEntities newInstance()
	{
		return new NetworkAssembly();
	}

	/** Checks if the tile can run as well sucks up energy for the tile to run */
	public boolean doPowerRun(TileEntityAssembly tile)
	{
		if (tile != null && this.wattStored >= tile.getRequest())
		{
			this.wattStored -= tile.getRequest();
			return true;
		}
		return false;
	}

	/** Adds power to the network. Does not save power on area unload */
	public void addPower(double d)
	{
		this.wattStored += d;
	}

	/** Gets the amount of power this network needs
	 * 
	 * @param total - true for total network, false for amount equal to each power connection */
	public double getRequest()
	{
		double watt = 1;
		for (INetworkPart part : this.getNetworkMemebers())
		{
			if (part instanceof TileEntityAssembly)
			{
				watt += ((TileEntityAssembly) part).getRequest();
			}
		}
		return watt;
	}

	public double getMaxBattery()
	{
		return this.getRequest() * 4;
	}

	public double getCurrentBattery()
	{
		return this.wattStored;
	}

	@Override
	public void mergeDo(NetworkTileEntities network)
	{
		NetworkAssembly newNetwork = new NetworkAssembly();
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpMembers();
	}

	@Override
	public boolean isValidMember(INetworkPart part)
	{
		return super.isValidMember(part) && part instanceof TileEntityAssembly;
	}

	@Override
	public String toString()
	{
		return "AssemblyNetwork[" + this.hashCode() + "|parts:" + this.networkMember.size() + "]";
	}

}
