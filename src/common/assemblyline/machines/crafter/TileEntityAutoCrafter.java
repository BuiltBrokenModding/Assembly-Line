package assemblyline.machines.crafter;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import assemblyline.TileEntityBase;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityAutoCrafter extends TileEntityBase implements ISidedInventory
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

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 0;
	}
}
