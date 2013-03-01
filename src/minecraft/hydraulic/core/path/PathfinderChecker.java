package hydraulic.core.path;

import hydraulic.core.implement.ILiquidConnectionProvider;
import hydraulic.core.implement.ILiquidConnector;

import java.util.Arrays;
import java.util.List;

import universalelectricity.core.vector.Vector3;

import net.minecraftforge.common.ForgeDirection;

/**
 * Check if a conductor connects with another.
 * 
 * @author Calclavia, DarkGuardsman
 * 
 */
public class PathfinderChecker extends Pathfinder
{
	public PathfinderChecker(final Vector3 targetConnector,final List<Integer> blockIds, final Vector3... ignoreConnector)
	{
		super(new IPathCallBack()
		{
			@Override
			public boolean isValidNode(Pathfinder finder, ForgeDirection direction, Vector3 provider, Vector3 connectedBlock)
			{
				return !Arrays.asList(ignoreConnector).contains(connectedBlock);
			}

			@Override
			public boolean onSearch(Pathfinder finder, Vector3 provider)
			{
				if (provider == targetConnector)
				{
					finder.results.add(provider);
					return true;
				}

				return false;
			}
		}, blockIds);
	}
}
