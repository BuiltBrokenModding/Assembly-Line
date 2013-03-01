package hydraulic.core.implement;

import net.minecraftforge.common.ForgeDirection;

/**
 * Applied to TileEntities that can connect to an electrical network.
 * 
 * @author Calclavia
 * 
 */
public interface ILiquidConnector
{

	/**
	 * @return If the connection is possible.
	 */
	public boolean canConnect(ForgeDirection direction);
}
