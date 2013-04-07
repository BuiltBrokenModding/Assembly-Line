package fluidmech.common.pump.path;

import fluidmech.common.FluidMech;
import fluidmech.common.pump.TileEntityDrain;
import hydraulic.helpers.FluidHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.path.IPathCallBack;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.vector.Vector3;

public class PathfinderCheckerFindAir extends Pathfinder
{
	public List<Vector3> targetList = new ArrayList<Vector3>();

	public PathfinderCheckerFindAir(final World world)
	{
		super(new IPathCallBack()
		{
			@Override
			public Set<Vector3> getConnectedNodes(Pathfinder finder, Vector3 currentNode)
			{
				Set<Vector3> neighbors = new HashSet<Vector3>();
				int fillable = 0;
				for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
				{
					if (direction != ForgeDirection.UP)
					{
						Vector3 pos = currentNode.clone().modifyPositionFromSide(direction);
						LiquidStack liquid = FluidHelper.getLiquidFromBlockId(pos.getBlockID(world));
						if (liquid != null || pos.getBlockID(world) == 0)
						{
							if(isFillableBlock(world,pos))
							{
								fillable++;
							}
							neighbors.add(pos);
						}
					}
				}

				return neighbors;
			}

			@Override
			public boolean onSearch(Pathfinder finder, Vector3 node)
			{
				if (finder.closedSet.size() >= 2000)
				{
					return true;
				}

				return false;
			}
		});
	}

	public static boolean isFillableBlock(World world, Vector3 vec)
	{
		LiquidStack liquid = FluidHelper.getLiquidFromBlockId(vec.getBlockID(world));
		if ((liquid != null && vec.getBlockMetadata(world) != 0) || vec.getBlockID(world) == 0)
		{
			return true;
		}
		return false;
	}
}
