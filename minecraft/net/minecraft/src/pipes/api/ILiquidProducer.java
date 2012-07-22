package net.minecraft.src.eui.pipes.api;

/**
 * The UEIProducer interface is an interface that must be applied to all tile entities that can produce electricity.
 * @author Calclavia
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
	public int onProduceLiquid(int type, int maxVol, int side);
	/**
	 * canProduceLiquid  
	 * block.
	 * @param type - the type of liquid 
	 * @param side - The side 
	 * @return boolean - True if can, false if can't produce liquid of type or on that side
	 * Also used for connection rules of pipes'
	 */
	public boolean canProduceLiquid(int type, byte side);
}