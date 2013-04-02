package hydraulic.fluidnetwork;

import hydraulic.api.IPipeConnection;

import java.util.Arrays;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IConnectionProvider;
import universalelectricity.core.path.Pathfinder;

/**
 * Check if a conductor connects with another.
 */
public class PathfinderCheckerPipes extends Pathfinder
{
	public PathfinderCheckerPipes(final IConnectionProvider targetConnector, final IConnectionProvider... ignoreConnector)
	{
		super(new IPathCallBack()
		{
			@Override
			public boolean isValidNode(Pathfinder finder, ForgeDirection direction, IConnectionProvider provider, IConnectionProvider connectedBlock)
			{
				if (connectedBlock instanceof IPipeConnection && !Arrays.asList(ignoreConnector).contains(connectedBlock))
				{
					if (((IPipeConnection) connectedBlock).canConnect((TileEntity) connectedBlock, direction.getOpposite()))
					{
						return true;
					}
				}
				return false;
			}

			@Override
			public boolean onSearch(Pathfinder finder, IConnectionProvider provider)
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
