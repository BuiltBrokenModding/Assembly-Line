package dark.core.tile.network;

import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.block.IElectricalStorage;
import dark.core.api.INetworkPart;

/** Used for tile networks that only need to share power or act like a group battery that doesn't
 * store power on world save
 *
 * @author DarkGuardsman */
public class NetworkSharedPower extends NetworkTileEntities
{
	private float sharedPower = 0;
	private float maxPower = 1;

	public NetworkSharedPower(INetworkPart... parts)
	{
		super(parts);
	}

	@Override
	public NetworkTileEntities newInstance()
	{
		return new NetworkSharedPower();
	}

	public float dumpPower(TileEntity source, float power, boolean doFill)
	{
		float room = (maxPower - sharedPower);
		if (this.networkMember.contains(source) && Math.ceil(room) > 0)
		{
			if (doFill)
			{
				this.sharedPower = Math.max(this.sharedPower + power, this.maxPower);
			}
			return Math.max(Math.min(Math.abs(room - power), power), 0);
		}
		return 0;
	}

	public boolean drainPower(TileEntity source, float power, boolean doDrain)
	{
		if (this.networkMember.contains(source) && this.sharedPower >= power)
		{
			if (doDrain)
			{
				this.sharedPower -= power;
			}
			return true;
		}
		return false;
	}

	@Override
	public void cleanUpMembers()
	{
		super.cleanUpMembers();
		this.maxPower = 0;
		for (INetworkPart part : this.networkMember)
		{
			if (part instanceof IElectricalStorage)
			{
				this.maxPower += ((IElectricalStorage) part).getMaxEnergyStored();
			}
		}

	}

}
