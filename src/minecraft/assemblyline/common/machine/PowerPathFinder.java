package assemblyline.common.machine;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.path.IPathCallBack;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.vector.Vector3;

public class PowerPathFinder extends Pathfinder
{
	public PowerPathFinder(final World world, final Vector3 start, final Vector3 goal, final int distance)
	{
		super(new pathCallBack(world, start, goal, distance));
	}

	public static class pathCallBack implements IPathCallBack
	{
		World world;
		Vector3 start, end;
		int distance;

		public pathCallBack(final World world, final Vector3 start, final Vector3 end, int distance)
		{
			this.world = world;
			this.start = start;
			this.end = end;
			this.distance = distance;
		}

		@Override
		public Set<Vector3> getConnectedNodes(Pathfinder finder, Vector3 currentNode)
		{
			Set<Vector3> neighbors = new HashSet<Vector3>();
			//TODO change this to get the connected tiles from the tile itself so to allow for non-forge direction connections
			for (int i = 0; i < 6; i++)
			{
				ForgeDirection direction = ForgeDirection.getOrientation(i);
				Vector3 position = currentNode.clone().modifyPositionFromSide(direction);
				TileEntity connectedBlock = position.getTileEntity(world);

				if (connectedBlock instanceof TileEntityAssembly && position.distanceTo(start) <= distance)
				{
					if (((TileEntityAssembly) connectedBlock).canTileConnect(connectedBlock, direction.getOpposite()))
					{
						neighbors.add(position);
					}
				}
			}

			return neighbors;
		}

		@Override
		public boolean onSearch(Pathfinder finder, Vector3 node)
		{
			if (node.equals(this.end))
			{
				finder.results.add(node);
				return true;
			}

			return false;
		}
	}
}
