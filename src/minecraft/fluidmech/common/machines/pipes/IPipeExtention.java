package fluidmech.common.machines.pipes;

import universalelectricity.prefab.network.IPacketReceiver;

public interface IPipeExtention extends IPacketReceiver
{
	public boolean canBePlacedOnPipe(TileEntityPipe pipe);

	public TileEntityPipe getPipe();

	public void setPipe(TileEntityPipe pipe);

	/**
	 * how many ticks before next update
	 */
	public int updateTick();

	/**
	 * if this sub tile needs a packet update
	 * 
	 * Note it pulls the packet from description packet
	 */
	public boolean shouldSendPacket();

}
