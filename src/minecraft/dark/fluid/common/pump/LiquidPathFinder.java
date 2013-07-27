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
    /** Curent world this pathfinder will operate in */
    private World world;
    /** List of all nodes traveled by the path finder */
    public List<Vector3> nodes = new ArrayList<Vector3>();
    /** List of all nodes that match the search parms */
    public List<Vector3> results = new ArrayList<Vector3>();
    /** Are we looking for liquid fillable blocks */
    private boolean fill = false;
    /** priority search direction either up or down only */
    private ForgeDirection priority;
    /** Limit on the searched nodes per run */
    private int resultLimit = 2000;
    /** Start location of the pathfinder used for range calculations */
    private Vector2 Start;
    /** Range to limit the search to */
    private double range;
    /** List of forgeDirection to use that are shuffled to prevent strait lines */
    List<ForgeDirection> shuffledDirections = new ArrayList<ForgeDirection>();

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
        shuffledDirections.add(ForgeDirection.EAST);
        shuffledDirections.add(ForgeDirection.WEST);
        shuffledDirections.add(ForgeDirection.NORTH);
        shuffledDirections.add(ForgeDirection.SOUTH);
    }

    /** Searches for nodes attached to the given node
     *
     * @return True on success finding, false on failure. */
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

            if (this.fill && FluidHelper.isFillable(world, node))
            {
                this.results.add(node);
            }
            else if (!this.fill && FluidHelper.drainBlock(world, node, false) != null)
            {
                this.results.add(node);
            }

            if (this.isDone(node.clone()))
            {
                return false;
            }

            if (find(this.priority, node.clone()))
            {
                return true;
            }

            Collections.shuffle(shuffledDirections);
            Collections.shuffle(shuffledDirections);

            for (ForgeDirection direction : shuffledDirections)
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

    /** Find all node attached to the origin mode in the given direction
     *
     * Note: Calls findNode if the next code is valid */
    public boolean find(ForgeDirection direction, Vector3 origin)
    {
        Vector3 vec = origin.clone().modifyPositionFromSide(direction);
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

    /** Checks to see if this node is valid to path find threw */
    public boolean isValidNode(Vector3 pos)
    {
        return FluidHelper.drainBlock(world, pos, false) != null || FluidHelper.isFillable(world, pos);
    }

    /** Checks to see if we are done pathfinding */
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
