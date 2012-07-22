package net.minecraft.src.eui.pipes.api;


public interface ILiquidConsumer
{
	/**
	 * onRecieveLiquid
	 * @param vol - The amount this block received.
	 * @param side - The side of the block in which the liquid came from.
	 * @parm type - The type of liquid being received
	 * @return vol - The amount liquid that can't be recieved
	 */
	public int onReceiveLiquid(int type, int vol, byte side);
	
	/**
	 * You can use this to check if a pipe can connect to this liquid consumer to properly render the graphics
	 * @param side - The side in which the electricity is coming from.
	 * @parm type - The type of liquid 
	 * @return Returns true or false if this consumer can receive electricity at this given tick or moment.
	 */
	public boolean canRecieveLiquid(int type, byte side);
	
	/**
	 * @return Return the stored liquid of type in this consumer.
	 */
	public int getStoredLiquid(int type);
	
	/**
	 * @return Return the maximum amount of stored liquid this consumer can get.
	 */
	public int getLiquidCapacity(int type);
	
}
