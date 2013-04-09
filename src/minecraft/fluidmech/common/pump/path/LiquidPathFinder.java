package fluidmech.common.pump.path;

import hydraulic.helpers.FluidHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.vector.Vector3;

/**
 * A simpler pathfinder based on Calclavia's PathFinder from UE api
 */
public class LiquidPathFinder
{
	private World world; /* MC WORLD */
	public List<Vector3> nodes = new ArrayList<Vector3>(); /* LOCATIONs THE PATH FINDER HAS GONE OVER */
	public List<Vector3> results = new ArrayList<Vector3>();/* LOCATIONS THAT ARE VALID RESULTS */
	private boolean fill; /* ARE WE FILLING THE PATH OR DRAINING THE PATH */
	private ForgeDirection priority; /* BASED ON fill -- WHICH DIRECTION WILL THE PATH GO FIRST */
	private int resultLimit = 2000;

	public LiquidPathFinder(final World world, final boolean fill, int resultLimit)
	{
		this.world = world;
		this.fill = fill;
		if (fill)
		{
			priority = ForgeDirection.DOWN;
		}
		else
		{
			priority = ForgeDirection.UP;
		}
		this.resultLimit = resultLimit;
		this.reset();
	}

	/**
	 * @return True on success finding, false on failure.
	 */
	public boolean findNodes(Vector3 node)
	{
		this.nodes.add(node);
		if (this.fill && (node.getBlockID(world) == 0 || (FluidHelper.getLiquidFromBlockId(node.getBlockID(world)) != null && node.getBlockMetadata(world) != 0)))
		{
			this.results.add(node);
		}
		else if (!this.fill && FluidHelper.isSourceBlock(world, node))
		{
			this.results.add(node);
		}

		if (this.isDone(node))
		{
			return false;
		}

		Vector3 vec = node.clone().modifyPositionFromSide(this.priority);
		if (this.isValidNode(vec) & !this.nodes.contains(vec))
		{
			if (this.findNodes(vec))
			{
				return true;
			}
		}

		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
		{
			if (direction != this.priority)
			{
				vec = node.clone().modifyPositionFromSide(direction);
				if (this.isValidNode(vec) & !this.nodes.contains(vec))
				{
					if (this.findNodes(vec))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isValidNode(Vector3 pos)
	{
		int blockID = pos.getBlockID(world);
		if (!this.fill)
		{
			return FluidHelper.getLiquidFromBlockId(pos.getBlockID(world)) != null;
		}
		else
		{
			return FluidHelper.getLiquidFromBlockId(pos.getBlockID(world)) != null || (blockID == 0 && FluidHelper.getConnectedSources(world, pos) > 0);
		}
	}

	public boolean isDone(Vector3 vec)
	{
		if (this.results.size() >= this.resultLimit || this.nodes.size() >= 10000)
		{
			return true;
		}
		return false;
	}

	/**
	 * Called to execute the pathfinding operation.
	 */
	public LiquidPathFinder init(Vector3 startNode)
	{
		this.findNodes(startNode);
		if (this.fill && this.isValidNode(startNode))
		{

		}
		return this;
	}

	public LiquidPathFinder reset()
	{
		this.nodes.clear();
		this.results.clear();
		return this;
	}
}
