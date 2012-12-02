package basicpipes.pipes.api;

import net.minecraftforge.common.ForgeDirection;

/**
 * Based off of Calclavia's old wire API
 * @author DarkGuardsman
 *
 */
public interface ILiquidProducer
{
	/**
	 * onProduceLiquid  
	 * block.
	 * @param type - the type of liquid 
	 * @param maxvol - The maximum vol or requested volume
	 * @param side - The side 
	 * @return vol - Return a vol of liquid type that is produced
	 */
	public int onProduceLiquid(Liquid type, int maxVol, ForgeDirection side);
	/**
	 * canProduceLiquid  
	 * block.
	 * @param type - the type of liquid 
	 * @param side - The side 
	 * @return boolean - True if can, false if can't produce liquid of type or on that side
	 * Also used for connection rules of pipes'
	 */
	public boolean canProduceLiquid(Liquid type, ForgeDirection side);
	public boolean canProducePresure(Liquid type, ForgeDirection side);
	/**
	 * 
	 * @param type - liquid type
	 * @param side - side this of presure
	 * @return pressure that is used to output liquid on
	 */
	public int presureOutput(Liquid type, ForgeDirection side);
}