package assemblyline.common.machine.crane;

import net.minecraftforge.common.ForgeDirection;
import assemblyline.api.ICraneStructure;
import assemblyline.common.machine.TileEntityAssembly;

public class TileEntityCraneRail extends TileEntityAssembly implements ICraneStructure
{

	public TileEntityCraneRail()
	{
		super(0);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canFrameConnectTo(ForgeDirection side)
	{
		return true;
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return false;
	}

	@Override
	public void onUpdate()
	{
		// TODO Auto-generated method stub
		
	}

}
