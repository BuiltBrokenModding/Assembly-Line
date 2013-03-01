package hydraulic.core.path;

import hydraulic.core.implement.ILiquidConnectionProvider;
import hydraulic.core.implement.ILiquidConnector;

import java.util.Arrays;

import net.minecraftforge.common.ForgeDirection;

/**
 * Check if a conductor connects with another.
 * 
 * @author Calclavia
 * 
 */
public class PathfinderChecker extends Pathfinder
{
	public PathfinderChecker(final ILiquidConnectionProvider targetConnector, final ILiquidConnectionProvider... ignoreConnector)
	{
		super(new IPathCallBack()
		{
			@Override
			public boolean isValidNode(Pathfinder finder, ForgeDirection direction, ILiquidConnectionProvider provider, ILiquidConnectionProvider connectedBlock)
			{
				if (connectedBlock instanceof ILiquidConnector && !Arrays.asList(ignoreConnector).contains(connectedBlock))
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
				if (provider == targetConnector)
				{
					finder.results.add(provider);
					return true;
				}

				return false;
			}
		});
	}
}
