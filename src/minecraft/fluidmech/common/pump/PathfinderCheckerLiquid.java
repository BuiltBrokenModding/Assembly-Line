package fluidmech.common.pump;

import fluidmech.common.FluidMech;
import hydraulic.helpers.FluidHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.path.IPathCallBack;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.vector.Vector3;

/**
 * Check if a conductor connects with another.
 * 
 * @author Calclavia
 * 
 */
public class PathfinderCheckerLiquid extends Pathfinder
{
	public PathfinderCheckerLiquid(final World world, final int maxResources, final LiquidStack resource, final Vector3... ignoreList)
	{
		super(new IPathCallBack()
		{
			@Override
			public Set<Vector3> getConnectedNodes(Pathfinder finder, Vector3 currentNode)
			{
				Set<Vector3> neighbors = new HashSet<Vector3>();

				for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
				{
					Vector3 pos = currentNode.clone().modifyPositionFromSide(direction);

					if (FluidHelper.isStillLiquid(world,pos) && FluidHelper.getLiquidFromBlockId(pos.getBlockID(world)).equals(resource))
					{
						neighbors.add(pos);
					}
				}

				return neighbors;
			}

			@Override
			public boolean onSearch(Pathfinder finder, Vector3 node)
			{
				if (finder.closedSet.size() >= maxResources)
				{
					return true;
				}

				return false;
			}
		});
	}
}
