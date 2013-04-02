package fluidmech.common.machines.pipes;

import com.google.common.io.ByteArrayDataInput;

import fluidmech.client.render.pipeextentions.IPipeExtentionRender;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidStack;

public class TileEntityPipeWindow extends TileEntityPipeExtention
{

	private TileEntityPipe pipe = null;
	private boolean shouldUpdate = false;

	LiquidStack stack = new LiquidStack(0, 0, 0);

	@Override
	public void updateEntity()
	{
		if(!worldObj.isRemote && pipe != null)
		{			
			stack = pipe.getNetwork().getTank().getLiquid();
		}
		if(worldObj.isRemote)
		{
			System.out.println("Alive");
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
	public boolean shouldSendPacket(boolean server)
	{
		return shouldUpdate;
	}

	@Override
	public NBTTagCompound getExtentionPacketData(boolean server)
	{
		// TODO Auto-generated method stub
		return null;
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
	public IPipeExtentionRender getExtentionRenderClass()
	{
		return null;
	}
	@Override
	public String toString()
	{
		return "PipeWindow";
	}

}
