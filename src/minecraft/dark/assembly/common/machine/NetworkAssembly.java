package dark.assembly.common.machine;

import dark.api.INetworkPart;
import dark.core.tile.network.NetworkTileEntities;

public class NetworkAssembly extends NetworkTileEntities
{
	public NetworkAssembly(INetworkPart... parts)
	{
		super(parts);
	}

	public NetworkTileEntities newInstance()
	{
		return new NetworkAssembly();
	}

	@Override
	public boolean isValidMember(INetworkPart part)
	{
		return super.isValidMember(part) && part instanceof TileEntityAssembly;
	}

	@Override
	public String toString()
	{
		return "AssemblyNetwork[" + this.hashCode() + "][parts:" + this.networkMember.size() + "]";
	}

}
