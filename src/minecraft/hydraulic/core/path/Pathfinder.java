package hydraulic.core.path;

import hydraulic.helpers.connectionHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

/**
 * A class that allows flexible path finding in Minecraft Blocks.
 * 
 * @author Calclavia, DarkGuardsman
 * 
 */
public class Pathfinder
{
	public Vector3 target;
	/**
	 * A list of nodes that the pathfinder went through.
	 */
	public List<Vector3> searchedNodes;
	/**
	 * A list of valid block IDs to use as a path.
	 */
	public List<Integer> blockIDs;

	/**
	 * The results and findings found by the pathfinder.
	 */
	public List results;

	public World world;

	public Pathfinder(World world, List<Integer> blockIDs)
	{
		this.blockIDs = blockIDs;
		this.world = world;
		this.clear();
	}

	public boolean findNodes(Vector3 location)
	{
		int[] connectedBlocks = connectionHelper.getSurroundingBlocks(world, location);

		this.searchedNodes.add(location);

		if (this.onSearch(location))
		{
			return false;
		}

		for (int i = 0; i < connectedBlocks.length; i++)
		{

			if (blockIDs.contains(connectedBlocks[i]))
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				Vector3 dirLoc = new Vector3(location.intX() + dir.offsetX, location.intY() + dir.offsetY, location.intZ() + dir.offsetZ);

				if (!searchedNodes.contains(dirLoc) && this.isValidNode(dir, dirLoc))
				{
					if (!this.findNodes(dirLoc))
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Is this a valid node to search for?
	 * 
	 * @return
	 */
	public boolean isValidNode(ForgeDirection direction, Vector3 foundNode)
	{
		return true;
	}

	/**
	 * Called when looping through nodes.
	 * 
	 * @param finder
	 * @param provider
	 * @return True to stop the path finding operation.
	 */
	public boolean onSearch(Vector3 location)
	{
		if (location == target)
		{
			this.results.add(location);
			return true;
		}
		return false;
	}

	/**
	 * Called to execute the pathfinding operation.
	 */
	public Pathfinder init(Vector3 start, Vector3 target)
	{
		this.target = target;
		this.findNodes(start);		
		return this;
	}

	public Pathfinder clear()
	{
		this.searchedNodes = new ArrayList<Vector3>();
		this.results = new ArrayList();
		return this;
	}
}
