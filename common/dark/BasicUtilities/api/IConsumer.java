package dark.BasicUtilities.api;

import net.minecraftforge.common.ForgeDirection;
/**
 * Based off of Calclavia's old wire API
 * @author DarkGuardsman
 *
 */

public interface IConsumer
{
	/**
	 * onRecieveLiquid
	 * @param vol - The amount this block received.
	 * @param side - The side of the block in which the liquid came from.
	 * @parm type - The type of liquid being received
	 * @return vol - The amount liquid that can't be recieved
	 */
	public int onReceiveLiquid(Liquid type, int vol, ForgeDirection side);
	
	/**
	 * You can use this to check if a pipe can connect to this liquid consumer to properly render the graphics
	 * @param forgeDirection - The side in which the volume is coming from.
	 * @parm type - The type of liquid 
	 * @return Returns true or false if this consumer can receive a volume at this given tick or moment.
	 */
	public boolean canRecieveLiquid(Liquid type, ForgeDirection forgeDirection);
	
	/**
	 * @return Return the stored liquid of type in this consumer.
	 */
	public int getStoredLiquid(Liquid type);
	
	/**
	 * @return Return the maximum amount of stored liquid this consumer can get.
	 */
	public int getLiquidCapacity(Liquid type);
	
}
