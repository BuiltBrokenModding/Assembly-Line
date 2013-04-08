package fluidmech.common.pump.path;

import hydraulic.helpers.FluidHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.path.IPathCallBack;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.path.PathfinderAStar;
import universalelectricity.core.vector.Vector3;

/**
 * Check if a conductor connects with another.
 * 
 * @author Calclavia
 * 
 */
public class PathfinderFindPathThrewWater extends PathfinderAStar
{
	public PathfinderFindPathThrewWater(final World world, final Vector3 goal)
	{
		super(new IPathCallBack()
		{
			@Override
			public Set<Vector3> getConnectedNodes(Pathfinder finder, Vector3 currentNode)
			{
				Set<Vector3> neighbors = new HashSet<Vector3>();

				for (int i = 0; i < 6; i++)
				{
					ForgeDirection direction = ForgeDirection.getOrientation(i);
					Vector3 position = currentNode.clone().modifyPositionFromSide(direction);

					if (FluidHelper.getLiquidFromBlockId(position.getBlockID(world)) != null || (position.getBlockID(world) == 0 && FluidHelper.getConnectedSources(world, position) > 0))
					{
						neighbors.add(position);
					}
				}

				return neighbors;
			}

			@Override
			public boolean onSearch(Pathfinder finder, Vector3 node)
			{
				if (node == goal)
				{
					finder.results.add(node);
					return true;
				}

				return false;
			}
		}, goal);
	}
}
