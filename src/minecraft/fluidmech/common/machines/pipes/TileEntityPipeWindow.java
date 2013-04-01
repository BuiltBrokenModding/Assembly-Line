package fluidmech.common.machines.pipes;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidStack;

public class TileEntityPipeWindow extends TileEntity implements IPipeExtention
{
	private TileEntityPipe pipe = null;
	private boolean shouldUpdate = false;

	LiquidStack stack = new LiquidStack(0, 0, 0);

	@Override
	public void updateEntity()
	{
		if(pipe != null)
		{			
			stack = pipe.getNetwork().getTank().getLiquid();
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{

		if(pipe != null && worldObj.isRemote)
		{
			
		}

	}

	@Override
	public boolean canBePlacedOnPipe(TileEntityPipe pipe)
	{
		return true;
	}

	@Override
	public TileEntityPipe getPipe()
	{
		return pipe;
	}

	@Override
	public void setPipe(TileEntityPipe pipe)
	{
		this.pipe = pipe;
	}

	@Override
	public int updateTick()
	{
		return 10;
	}

	@Override
	public boolean shouldSendPacket()
	{
		return shouldUpdate;
	}

}
