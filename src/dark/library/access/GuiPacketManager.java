package dark.library.access;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class GuiPacketManager extends PacketManager
{
	public enum GuiPacketType
	{
		USER_LISTS_REQUEST, USER_LISTS, LISTS_DATA, LISTS_DATA_REQUEST;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (player != null)
		{
			World world = player.worldObj;
			GuiPacketType packetID = GuiPacketType.values()[dataStream.readInt()];
			if (world.isRemote)
			{
				if (packetID == GuiPacketType.USER_LISTS)
				{

				}
				if (packetID == GuiPacketType.LISTS_DATA)
				{

				}
			}
			else
			{
				if (packetID == GuiPacketType.USER_LISTS_REQUEST)
				{

				}
			}
		}
	}

	public void requestData()
	{

	}
}
