package SteamPower.ap;


public interface IHeatProducer
{
	/**
	 * onProduceElectricity is called when a conductor is connected to the producer block in which the conductor will demand power from the producer
	 * block.
	 * @param jouls - The maximum jouls can be transfered
	 * @param side - The side of block in which the conductor is on
	 * @return jouls - Return jouls to consumer
	 */
	public float onProduceHeat(float jouls, int side);
}