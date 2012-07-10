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
	 * @param type - the type of liquid or gas
	 * @param maxvol - The maximum vol 
	 * @param side - The side 
	 * @return vol - Return a vol of liquid type
	 */
	public int onProduceLiquid(int type, int maxVol, int side);
	
	public boolean canProduceLiquid(int type, byte side);
	
	public boolean canConnectFromTypeAndSide(int type, byte side);
}