package dark.fluid.common.pump;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import dark.core.helpers.FluidHelper;

/** A simpler pathfinder based on Calclavia's PathFinder from UE api */
public class LiquidPathFinder
{
	private World world; /* MC WORLD */
	public List<Vector3> nodes = new ArrayList<Vector3>(); /*
															 * LOCATIONs THE PATH FINDER HAS GONE
															 * OVER
															 */
	public List<Vector3> results = new ArrayList<Vector3>();/* LOCATIONS THAT ARE VALID RESULTS */
	private boolean fill = false; /* ARE WE FILLING THE PATH OR DRAINING THE PATH */
	private ForgeDirection priority; /* BASED ON fill -- WHICH DIRECTION WILL THE PATH GO FIRST */
	private int resultLimit = 2000;
	private Vector2 Start;
	private double range;
	private Random random = new Random();
	List<ForgeDirection> bn = new ArrayList<ForgeDirection>();

	public LiquidPathFinder(final World world, final int resultLimit, final double range)
	{
		this.range = range;
		this.world = world;
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
		bn.add(ForgeDirection.EAST);
		bn.add(ForgeDirection.WEST);
		bn.add(ForgeDirection.NORTH);
		bn.add(ForgeDirection.SOUTH);
	}

	/** @return True on success finding, false on failure. */
	public boolean findNodes(Vector3 node)
	{
		if (node == null)
		{
			return true;
		}
		try
		{
			Vector3 vec = node.clone();
			this.nodes.add(node);
			Chunk chunk = this.world.getChunkFromBlockCoords(vec.intX(), vec.intZ());

			if (chunk == null || !chunk.isChunkLoaded)
			{
				return true;
			}

			int id = node.getBlockID(world);
			int meta = node.getBlockID(world);
			if (this.fill && (id == 0 || (FluidHelper.isFillable(world, node))))
			{
				this.results.add(node);
			}
			else if (!this.fill && FluidHelper.drainBlock(world, node, false) != null)
			{
				this.results.add(node);
			}

			if (this.isDone(node))
			{
				return false;
			}

			if (find(this.priority, node.clone()))
			{
				return true;
			}

			Collections.shuffle(bn);
			Collections.shuffle(bn);

			for (ForgeDirection direction : bn)
			{
				if (find(direction, vec))
				{
					return true;
				}
			}

			if (find(this.priority.getOpposite(), node.clone()))
			{
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean find(ForgeDirection direction, Vector3 vec)
	{
		vec = vec.clone().modifyPositionFromSide(direction);
		double distance = vec.toVector2().distanceTo(this.Start);
		if (distance <= this.range && this.isValidNode(vec) & !this.nodes.contains(vec))
		{
			if (this.findNodes(vec))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isValidNode(Vector3 pos)
	{
		return FluidHelper.drainBlock(world, pos, false) != null;
	}

	public boolean isDone(Vector3 vec)
	{
		if (this.results.size() >= this.resultLimit || this.nodes.size() >= 4000)
		{
			return true;
		}
		return false;
	}

	/** Called to execute the pathfinding operation. */
	public LiquidPathFinder init(final Vector3 startNode, final boolean fill)
	{
		this.Start = startNode.toVector2();
		this.fill = fill;
		this.findNodes(startNode);
		return this;
	}

	public LiquidPathFinder reset()
	{
		this.nodes.clear();
		this.results.clear();
		return this;
	}
}
