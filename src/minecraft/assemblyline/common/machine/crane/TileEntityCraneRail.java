package assemblyline.common.machine.crane;

import net.minecraftforge.common.ForgeDirection;
import assemblyline.common.machine.TileEntityAssemblyNetwork;

public class TileEntityCraneRail extends TileEntityAssemblyNetwork implements ICraneConnectable
{

	@Override
	public boolean canFrameConnectTo(ForgeDirection side)
	{
		return true;
	}

}
