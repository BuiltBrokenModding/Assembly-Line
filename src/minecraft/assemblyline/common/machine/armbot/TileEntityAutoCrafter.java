package assemblyline.common.machine.armbot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
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
