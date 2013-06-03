package dark.fluid.api.mech;

import net.minecraftforge.common.ForgeDirection;

public interface IForceProvider
{
	/**
	 * 
	 * @param side the rpm is coming from
	 * @return rpm that the block is running at
	 */
	public int getForceSide(ForgeDirection side);

	/**
	 * 
	 * @param side
	 * @return if mechanical force can be outputed from this side
	 */
	public boolean canOutputSide(ForgeDirection side);
}
