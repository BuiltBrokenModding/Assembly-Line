package dark.api.mech;

import net.minecraftforge.common.ForgeDirection;

public interface IForceLoad
{
	/** @param side
	 * @return if mechanical force can be inputed from this side */
	public boolean canInputSide(ForgeDirection side);

	public int applyForce(ForgeDirection side, int force);
}
