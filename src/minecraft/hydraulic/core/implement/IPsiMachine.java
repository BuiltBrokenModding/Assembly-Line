package hydraulic.core.implement;

import net.minecraftforge.common.ForgeDirection;

public interface IPsiMachine
{
    /**
     * gets the devices pressure from a given side
     */
	public double getMaxPressure(ForgeDirection side);

}
