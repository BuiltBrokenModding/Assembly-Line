package fluidmech.common.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import hydraulic.prefab.tile.TileEntityFluidDevice;

public class TileEntityDrain extends TileEntityFluidDevice
{
	private ForgeDirection face = ForgeDirection.UNKNOWN;

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return null;
	}

	@Override
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
	{
		return dir == face.getOpposite();
	}

}
