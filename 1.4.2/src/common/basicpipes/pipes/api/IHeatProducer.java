package basicpipes.pipes.api;

import net.minecraftforge.common.ForgeDirection;


public interface IHeatProducer
{
	/**
	 * onProduceElectricity is called when a conductor is connected to the producer block in which the conductor will demand power from the producer
	 * block.
	 * @param jouls - The maximum jouls can be transfered
	 * @param up - The side of block in which the conductor is on
	 * @return jouls - Return jouls to consumer
	 */
	public float onProduceHeat(float jouls, ForgeDirection up);
}