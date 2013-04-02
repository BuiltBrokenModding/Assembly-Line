package fluidmech.common.machines.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityAdvanced;

import com.google.common.io.ByteArrayDataInput;

/**
 * Pipe Extension for the TileEntityPipe.class is a sub TileEntity and is not loaded the same way as
 * a normal TileEntity
 * 
 * @author Rseifert
 * 
 */
public abstract class TileEntityPipeExtention extends TileEntityAdvanced implements IPipeExtention, IPacketReceiver
{

	private TileEntityPipe masterPipe = null;
	public ForgeDirection direction = ForgeDirection.UNKNOWN;

	@Override
	public void initiate()
	{
		if (this.masterPipe != null)
		{
			System.out.println("Sending TileEntity to Client from extention.class");
			masterPipe.sendExtentionToClient(direction.ordinal());
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public TileEntityPipe getPipe()
	{
		return this.masterPipe;
	}

	@Override
	public void setPipe(TileEntityPipe pipe)
	{
		this.masterPipe = pipe;
	}

	@Override
	public void setDirection(ForgeDirection dir)
	{
		this.direction = dir;

	}

	@Override
	public ForgeDirection getDirection()
	{
		return this.direction;
	}

	@Override
	public String toString()
	{
		return "PipeExtention";
	}
}
