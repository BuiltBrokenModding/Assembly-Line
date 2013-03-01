package hydraulic.core.path;

import hydraulic.core.implement.ILiquidConnectionProvider;
import hydraulic.core.implement.ILiquidConnector;
import net.minecraftforge.common.ForgeDirection;

/**
 * Finds all the possible conductors.
 * 
 * @author Calclavia
 * 
 */
public class PathfinderConductor extends Pathfinder
{
	public PathfinderConductor()
	{
		super(new IPathCallBack()
		{
			@Override
			public boolean isValidNode(Pathfinder finder, ForgeDirection direction, ILiquidConnectionProvider provider, ILiquidConnectionProvider connectedBlock)
			{
				if (connectedBlock instanceof ILiquidConnector)
				{
					if (((ILiquidConnector) connectedBlock).canConnect(direction.getOpposite()))
					{
						return true;
					}
				}
				return false;
			}

			@Override
			public boolean onSearch(Pathfinder finder, ILiquidConnectionProvider provider)
			{
				return false;
			}
		});
	}
}
