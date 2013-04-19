package hydraulic.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public interface IPipeConnection
{
	/**
	 * This method should only be used by pipe like objects to find if they can connect to this
	 * object
	 * 
	 * @param entity - the pipe connecting to this object as a TileEntity instance
	 * @param dir - side connecting too
	 * 
	 * @return true if it can connect
	 */
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir);
}
