package hydraulic.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public interface IPipeConnection
{
	/**
	 * 
	 * @param ent - tileEntity trying to connect to this machine
	 * @param stack - liquid(s) it can accept. It will pass null if the connecting machine has no
	 * specific stack requirement
	 * @return true if it can connect
	 */
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir);
}
