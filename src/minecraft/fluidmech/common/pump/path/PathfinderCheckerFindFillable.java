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

public class PathfinderCheckerFindFillable extends Pathfinder
{
	public List<Vector3> targetList = new ArrayList<Vector3>();

	public PathfinderCheckerFindFillable(final World world)
	{
		super(new IPathCallBack()
		{
			@Override
			public Set<Vector3> getConnectedNodes(Pathfinder finder, Vector3 currentNode)
			{
				Set<Vector3> neighbors = new HashSet<Vector3>();
				int sources = 0;
				
				Vector3 pos = currentNode.clone().modifyPositionFromSide(ForgeDirection.DOWN);
				LiquidStack liquid = FluidHelper.getLiquidFromBlockId(pos.getBlockID(world));
				/* SEARCH DOWN */
				if ((liquid != null || pos.getBlockID(world) == 0) && FluidHelper.getConnectedSources(world, pos) > 0)
				{
					neighbors.add(pos);
					return neighbors;
				}
				/* SEARCH AROUND - UP SEARCH IS DONE BY THE OBJECT USING THIS PATHFINDER */
				for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
				{
					if (direction != ForgeDirection.UP && direction != ForgeDirection.DOWN)
					{
						pos = currentNode.clone().modifyPositionFromSide(direction);
						liquid = FluidHelper.getLiquidFromBlockId(pos.getBlockID(world));
						if ((liquid != null || pos.getBlockID(world) == 0) && FluidHelper.getConnectedSources(world, pos) > 0)
						{
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

	
}
