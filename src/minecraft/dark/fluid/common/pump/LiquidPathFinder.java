package dark.fluid.common.pump;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import dark.core.helpers.FluidHelper;

/** A simpler path Finder used to find drainable or fillable tiles
 *
 * @author DarkGuardsman */
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
            this.nodes.add(node);

            if (this.isValidResult(node))
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
                if (find(direction, node.clone()))
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
        if (pos == null)
        {
            return false;
        }
        /* Check if the chunk is loaded to prevent action outside of the loaded area */
        Chunk chunk = this.world.getChunkFromBlockCoords(pos.intX(), pos.intZ());
        if (chunk == null || !chunk.isChunkLoaded)
        {
            return false;
        }
        /* Fillable blocks need to be connected to fillable fluid blocks to be valid */
        if (FluidHelper.isFillableBlock(world, pos))
        {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                if (FluidHelper.isFillableFluid(world, pos.clone().modifyPositionFromSide(dir)))
                {
                    return true;
                }
            }
        }
        return FluidHelper.drainBlock(world, pos, false) != null || FluidHelper.isFillableFluid(world, pos);
    }

    public boolean isValidResult(Vector3 node)
    {
        if (this.fill && (FluidHelper.isFillableBlock(world, node) || FluidHelper.isFillableFluid(world, node)))
        {
            return true;
        }
        else if (!this.fill && FluidHelper.drainBlock(world, node, false) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
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
    public LiquidPathFinder start(final Vector3 startNode, final boolean fill)
    {
        this.Start = startNode.toVector2();
        this.fill = fill;
        if (this.nodes.isEmpty())
        {
            this.findNodes(startNode);
        }
        else
        {
            this.find(ForgeDirection.UNKNOWN, startNode);
        }
        return this;
    }

    public LiquidPathFinder reset()
    {
        this.nodes.clear();
        this.results.clear();
        return this;
    }

    public LiquidPathFinder refresh()
    {
        Iterator<Vector3> it = this.nodes.iterator();
        while(it.hasNext())
        {
            if(!this.isValidNode(it.next()))
            {
                it.remove();
            }
        }
        it = this.results.iterator();
        while(it.hasNext())
        {
            if(!this.isValidResult(it.next()))
            {
                it.remove();
            }
        }
        return this;
    }
}
