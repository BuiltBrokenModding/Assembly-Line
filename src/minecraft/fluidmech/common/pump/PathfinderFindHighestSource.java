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

public class PathfinderFindHighestSource extends Pathfinder
{
	public static int highestY = 0;

	public PathfinderFindHighestSource(final World world, final LiquidStack resource, final Vector3... ignoreList)
	{
		super(new IPathCallBack()
		{
			@Override
			public Set<Vector3> getConnectedNodes(Pathfinder finder, Vector3 currentNode)
			{
				Set<Vector3> neighbors = new HashSet<Vector3>();
				
				Vector3 pos = currentNode.clone().modifyPositionFromSide(ForgeDirection.UP);
				LiquidStack liquid = FluidHelper.getLiquidFromBlockId(pos.getBlockID(world));
				if (pos.getBlockID(world) != 0 && liquid != null && (liquid.equals(resource) || resource == null))
				{
					neighbors.add(pos);
					if (pos.intY() > highestY)
					{
						highestY = pos.intY();
					}
					return neighbors;
				}
				
				for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
				{
					pos = currentNode.clone().modifyPositionFromSide(direction);
					liquid = FluidHelper.getLiquidFromBlockId(pos.getBlockID(world));
					if (pos.getBlockID(world) != 0 && liquid != null && (liquid.equals(resource) || resource == null))
					{
						neighbors.add(pos);
						if (pos.intY() > highestY)
						{
							highestY = pos.intY();
						}
					}
				}

				return neighbors;
			}

			@Override
			public boolean onSearch(Pathfinder finder, Vector3 node)
			{
				if (finder.closedSet.size() >= 1000 || highestY >= 255)
				{
					return true;
				}

				return false;
			}
		});
		this.highestY = 0;
	}
}
