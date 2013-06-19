package dark.core.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public interface ITileConnector
{
	/**
	 * Can this tile connect on the given side
	 */
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir);
}
