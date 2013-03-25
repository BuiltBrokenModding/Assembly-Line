package hydraulic.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

public interface IPipeConnector
{
	/**
	 * 
	 * @param ent - tileEntity trying to connect to this machine
	 * @param stack - liquid(s) it is most likely going to take or pass. It will pass null if it
	 * doesn't care
	 * @return true if it can connect
	 */
	public boolean canConnect(ForgeDirection dir, LiquidStack... stacks);
}
