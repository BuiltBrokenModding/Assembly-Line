package assemblyline.common.machine;

import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.electricity.ElectricityDisplay.ElectricUnit;
import universalelectricity.core.electricity.ElectricityPack;
import net.minecraft.tileentity.TileEntity;
import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkPowerTiles;
import dark.core.tile.network.NetworkTileEntities;

public class NetworkAssembly extends NetworkPowerTiles
{
	public NetworkAssembly(INetworkPart... parts)
	{
		super(parts);
	}

	public NetworkTileEntities newInstance()
	{
		return new NetworkAssembly();
	}

	/** Consumes power for the tile to run on
	 * 
	 * @param tile - tileEntity
	 * @return true if the power was consumed */
	public boolean consumePower(TileEntityAssembly tile)
	{
		if (tile != null && this.wattStored >= tile.getWattLoad())
		{
			this.wattStored -= tile.getWattLoad();
			//System.out.println("Power| ---" + ElectricityDisplay.getDisplaySimple(tile.getWattLoad(), ElectricUnit.WATT, 2) + " A: " + ElectricityDisplay.getDisplaySimple(this.wattStored, ElectricUnit.WATT, 2));
			return true;
		}
		return false;
	}

	/** Adds power to the network. Does not save power on area unload */
	public void addPower(double d)
	{
		double before = this.wattStored;
		this.wattStored = Math.max(this.wattStored + d, this.getMaxBattery());
		System.out.println("Power| +++" + ElectricityDisplay.getDisplaySimple(d, ElectricUnit.WATT, 2) + " A: " + ElectricityDisplay.getDisplaySimple(this.wattStored, ElectricUnit.WATT, 2));

	}

	@Override
	public ElectricityPack getRequest(TileEntity... ents)
	{
		ElectricityPack pack = super.getRequest(ents);
		if (pack == null || pack.voltage == 0 || pack.amperes == 0)
		{
			pack = new ElectricityPack(0, 120);
		}
		double watt = pack.getWatts();
		for (INetworkPart part : this.getNetworkMemebers())
		{
			//TODO do check for ignored tiles/ents
			if (part instanceof TileEntityAssembly)
			{
				watt += ((TileEntityAssembly) part).getWattLoad();
			}
		}
		return ElectricityPack.getFromWatts(watt, pack.voltage);
	}

	@Override
	public boolean isValidMember(INetworkPart part)
	{
		return super.isValidMember(part) && part instanceof TileEntityAssembly;
	}

	@Override
	public String toString()
	{
		return "AssemblyNetwork[" + this.hashCode() + "][parts:" + this.networkMember.size() + "][Power:" + ElectricityDisplay.getDisplaySimple(this.wattStored, ElectricUnit.WATT, 2) + "]";
	}

}
