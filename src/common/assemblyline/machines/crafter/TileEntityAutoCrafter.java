package assemblyline.machines.crafter;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import assemblyline.TileEntityBase;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityAutoCrafter extends TileEntityBase
{
	@Override
	public String getInvName()
	{
		return "Auto Crafter";
	}

	@Override
	public int getSizeInventory()
	{
		return 10;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		
	}
}
