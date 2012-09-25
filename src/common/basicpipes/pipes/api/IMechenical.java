package basicpipes.pipes.api;

import net.minecraftforge.common.ForgeDirection;
// mechanical 
public interface IMechenical {
/**
 * 
 * @param side the rpm is coming from
 * @return rpm that the block is running at
 */
	public int getRPM(ForgeDirection side);
	/**
	 * 
	 * @param side
	 * @return if mechanical force can be outputed from this side
	 */
	public boolean canOutputSide(ForgeDirection side);
	/**
	 * 
	 * @param side
	 * @return if mechanical force can be inputed from this side
	 */
	public boolean canInputSide(ForgeDirection side);
	/**
	 * 
	 * @param RPM being applied to this machine
	 * @return the rpm after the load has been applied
	 */
	public int useRPM(int RPM);//will change later to include force of rotation
}
