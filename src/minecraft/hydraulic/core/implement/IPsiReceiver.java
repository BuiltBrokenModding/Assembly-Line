package hydraulic.core.implement;

import net.minecraftforge.common.ForgeDirection;

public interface IPsiReceiver
{
    /**
     * gets the devices pressure from a given side
     */
    public int getInputPressure(ForgeDirection side);

}
