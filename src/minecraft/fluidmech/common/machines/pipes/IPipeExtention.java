package fluidmech.common.machines.pipes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import fluidmech.client.render.RenderPipeWindow;
import fluidmech.client.render.pipeextentions.IPipeExtentionRender;
import universalelectricity.prefab.network.IPacketReceiver;

public interface IPipeExtention extends IPacketReceiver
{
	public boolean canBePlacedOnPipe(TileEntityPipe pipe, int side);

	public TileEntityPipe getPipe();

	public void setPipe(TileEntityPipe pipe);

	/**
	 * how many ticks before next update
	 */
	public int updateTick();

	/**
	 * if this sub tile needs a packet update
	 * @param  
	 */
	public boolean shouldSendPacket(boolean server);

	/**
	 * data that will be sent to this extension
	 */
	public NBTTagCompound getExtentionPacketData(boolean server);

	/**
	 * render class to be used to render this pipe extension of the face of the main pipe
	 */
	public Class<?> getExtentionRenderClass();
	
	public void setDirection(ForgeDirection dir);
	
	public ForgeDirection getDirection();

}
