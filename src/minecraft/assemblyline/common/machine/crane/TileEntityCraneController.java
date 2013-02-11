package assemblyline.common.machine.crane;

import net.minecraftforge.common.ForgeDirection;
import assemblyline.common.machine.TileEntityAssemblyNetwork;

public class TileEntityCraneController extends TileEntityAssemblyNetwork implements ICraneConnectable
{
	@Override
	public void updateEntity()
	{

	}

	@Override
	public boolean canFrameConnectTo(ForgeDirection side)
	{
		ForgeDirection facing = ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		if (side == facing)
			return true;
		if (side == CraneHelper.rotateClockwise(facing))
			return true;
		if (side == ForgeDirection.UP)
			return true;
		return false;
	}
}
