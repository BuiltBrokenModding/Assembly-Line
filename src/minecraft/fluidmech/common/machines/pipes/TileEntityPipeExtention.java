package fluidmech.common.machines.pipes;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import universalelectricity.prefab.network.IPacketReceiver;

/**
 * Pipe Extension for the TileEntityPipe.class is a sub TileEntity and is not loaded the same way as
 * a normal TileEntity
 * 
 * @author Rseifert
 * 
 */
public abstract class TileEntityPipeExtention extends TileEntity implements IPipeExtention, IPacketReceiver
{

	private TileEntityPipe masterPipe = null;

	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{

	}

	/**
	 * Writes a tile entity to NBT.
	 */
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
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
	public String toString()
	{
		return "PipeExtention";
	}
}
