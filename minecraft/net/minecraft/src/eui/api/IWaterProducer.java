package net.minecraft.src.eui.api;

/**
 * The UEIProducer interface is an interface that must be applied to all tile entities that can produce electricity.
 * @author Calclavia
 *
 */
public interface IWaterProducer
{
	/**
	 * onProduceElectricity is called when a conductor is connected to the producer block in which the conductor will demand power from the producer
	 * block.
	 * @param maxvol - The maximum vol the steam pipe can take
	 * @param side - The side of block in which the conductor is on
	 * @return vol - Return the amount of vol that cam be moved at one time
	 */
	public int onProduceWater(int maxVol, int side);
	
	public boolean canProduceWater(byte side);
}