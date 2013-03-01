package hydraulic.core.path;

import hydraulic.core.helpers.connectionHelper;
import hydraulic.core.implement.ILiquidConnectionProvider;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.core.vector.Vector3;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

/**
 * A class that allows flexible path finding in Minecraft Blocks.
 * 
 * @author Calclavia, DarkGuardsman
 * 
 */
public class Pathfinder
{
	public interface IPathCallBack
	{
		/**
		 * Is this a valid node to search for?
		 * 
		 * @return
		 */
		public boolean isValidNode(Pathfinder finder, ForgeDirection direction, Vector3 provider, Vector3 node);

		/**
		 * Called when looping through nodes.
		 * 
		 * @param finder
		 * @param provider
		 * @return True to stop the path finding operation.
		 */
		public boolean onSearch(Pathfinder finder, Vector3 location);
	}

	/**
	 * A pathfinding call back interface used to call back on paths.
	 */
	public IPathCallBack callBackCheck;

	/**
	 * A list of nodes that the pathfinder went through.
	 */
	public List<Vector3> iteratedNodes;
	/**
	 * A list of valid block IDs to use as a path.
	 */
	public List<Integer> blockIDs;

	/**
	 * The results and findings found by the pathfinder.
	 */
	public List results;

	public Pathfinder(IPathCallBack callBack, List<Integer> blockIDs)
	{
		this.callBackCheck = callBack;
		this.blockIDs = blockIDs;
		this.clear();
	}

	public boolean findNodes(World world, Vector3 location)
	{
		int[] connectedBlocks = connectionHelper.getSurroundingBlocks(world, location);

		this.iteratedNodes.add(location);

		if (this.callBackCheck.onSearch(this, location))
		{
			return false;
		}

		for (int i = 0; i < connectedBlocks.length; i++)
		{

			if (blockIDs.contains(connectedBlocks[i]))
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				Vector3 dirLoc = new Vector3(location.intX() + dir.offsetX, location.intY() + dir.offsetY, location.intZ() + dir.offsetZ);
				if (!iteratedNodes.contains(dirLoc))
				{
					if (this.callBackCheck.isValidNode(this, ForgeDirection.getOrientation(i), location, dirLoc))
					{
						if (!this.findNodes(world, dirLoc))
						{
							return false;
						}

					}
				}
			}
		}

		return true;
	}

	/**
	 * Called to execute the pathfinding operation.
	 */
	public Pathfinder init(World world, Vector3 location)
	{
		this.findNodes(world, location);
		return this;
	}

	public Pathfinder clear()
	{
		this.iteratedNodes = new ArrayList<Vector3>();
		this.results = new ArrayList();
		return this;
	}
}
