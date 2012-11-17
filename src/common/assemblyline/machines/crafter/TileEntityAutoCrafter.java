package assemblyline.machines.crafter;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import universalelectricity.prefab.tile.TileEntityAdvanced;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityAutoCrafter extends TileEntityAdvanced
{
	public String getInvName()
	{
		return "Auto Crafter";
	}

	public int getSizeInventory()
	{
		return 10;
	}

	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{

	}

}
