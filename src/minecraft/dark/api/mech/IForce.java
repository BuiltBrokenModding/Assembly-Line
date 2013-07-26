package dark.api.mech;

import net.minecraftforge.common.ForgeDirection;

// mechanical
public interface IForce
{
    /** @param side the rpm is coming from
     * @return rpm that the block is running at */
    public int getForceSide(ForgeDirection side);

    /** @param side
     * @return if mechanical force can be outputed from this side */
    public boolean canOutputSide(ForgeDirection side);

    /** @param side
     * @return if mechanical force can be inputed from this side */
    public boolean canInputSide(ForgeDirection side);

    /** @param RPM being applied to this machine
     * @return the rpm after the load has been applied */
    public int applyForce(int force);

    /** not required but is handy to get animation position of some mechanical block
     * 
     * @return int between 0 -7 */
    public int getAnimationPos();
}
