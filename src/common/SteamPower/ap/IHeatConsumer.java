package SteamPower.ap;

/**
 * The IHeatConsumer interface is an interface that must be applied to all tile entities that can receive heat joules.
 * @author Darkguardsman code sourced from Calclavia
 *
 */
public interface IHeatConsumer
{
	/**
	 * onRecieveSteam is called whenever a Steam transmitter sends a packet of electricity to the consumer (which is this block).
	 * @param vol - The amount of steam this block received
	 * @param side - The side of the block in which the electricity came from.
	 * @return vol - The amount of rejected steam to be sent to back
	 */
	public float onReceiveHeat(float jouls, int side);
	
	/**
	 * @return Return the stored electricity in this consumer. Called by conductors to spread electricity to this unit.
	 */
	public float getStoredHeat();
}