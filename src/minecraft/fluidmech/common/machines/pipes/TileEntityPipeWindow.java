package fluidmech.common.machines.pipes;

import com.google.common.io.ByteArrayDataInput;

import fluidmech.client.render.RenderPipeWindow;
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
		super.updateEntity();
		System.out.println("Updating side " + (worldObj.isRemote ? "Client" : "Server") );
		if(!worldObj.isRemote && pipe != null)
		{			
			stack = pipe.getNetwork().getTank().getLiquid();
			worldObj.setBlockAndMetadataWithNotify(xCoord, yCoord+1, yCoord, 0, 0, 3);
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
		return 20;
	}	

	@Override
	public Class<RenderPipeWindow> getExtentionRenderClass()
	{
		return RenderPipeWindow.class;
	}
	@Override
	public String toString()
	{
		return "PipeWindow";
	}

}
