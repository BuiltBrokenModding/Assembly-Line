package assemblyline.common.machine.crane;

import net.minecraftforge.common.ForgeDirection;
import assemblyline.api.ICraneConnectable;
import assemblyline.api.ICraneStructure;
import assemblyline.common.machine.TileEntityAssemblyNetwork;

public class TileEntityCraneRail extends TileEntityAssemblyNetwork implements ICraneStructure
{

	@Override
	public boolean canFrameConnectTo(ForgeDirection side)
	{
		return true;
	}

}
