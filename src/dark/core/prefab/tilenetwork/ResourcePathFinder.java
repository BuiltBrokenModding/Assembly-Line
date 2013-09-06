package dark.core.prefab.tilenetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

/** Check if a conductor connects with another. */
public abstract class ResourcePathFinder
{
    /** Curent world this pathfinder will operate in */
    private World world;
    /** List of all nodes traveled by the path finder */
    public Set<Vector3> nodeList = new HashSet<Vector3>();
    /** List of all nodes that match the search parms */
    public Set<Vector3> results = new HashSet<Vector3>();
    /** Are we looking for liquid fillable blocks */
    private boolean fill = false;
    /** priority search direction either up or down only */
    private ForgeDirection priority;
    /** Limit on the searched nodes per run */
    private int resultLimit = 200;
    private int resultsFound = 0;
    private int resultRun = resultLimit;
    private int runs = 0;
    /** Start location of the pathfinder used for range calculations */
    private Vector3 Start;
    /** Range to limit the search to */
    private double range;
    /** List of forgeDirection to use that are shuffled to prevent strait lines */
    List<ForgeDirection> shuffledDirections = new ArrayList<ForgeDirection>();

    public ResourcePathFinder(final World world, final int resultLimit, final double range)
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

    public void addNode(Vector3 vec)
    {
        if (!this.nodeList.contains(vec))
        {
            this.nodeList.add(vec);
        }
    }

    public void addResult(Vector3 vec)
    {
        if (!this.results.contains(vec))
        {
            this.resultsFound++;
            this.results.add(vec);
        }
    }

    /** Searches for nodes attached to the given node
     *
     * @return True on success finding, false on failure. */
    public boolean findNodes(Vector3 node)
    {
        if (node == null)
        {
            return false;
        }
        try
        {
            this.addNode(node);

            if (this.isValidResult(node))
            {
                this.addResult(node);
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

    /** Finds a node in a give direction
     *
     * Note: Calls findNode if the next code is valid */
    public boolean find(ForgeDirection direction, Vector3 origin)
    {
        this.runs++;
        Vector3 vec = origin.clone().modifyPositionFromSide(direction);
        double distance = vec.toVector2().distanceTo(this.Start.toVector2());
        if (distance <= this.range && this.isValidNode(vec))
        {
            if (onFind())
            {
                return true;
            }
            if (this.findNodes(vec))
            {
                return true;
            }
        }
        return false;
    }

    /** Called when the pathfinder jumps to the next block. Use this to inject other processes into
     * the find method
     *
     * @return true if you found results, or just want the calling method to return true */
    public boolean onFind()
    {
        return false;
    }

    /** Checks to see if this node is valid to path find threw */
    public abstract boolean isValidNode(Vector3 pos);

    public abstract boolean isValidResult(Vector3 node);

    /** Checks to see if we are done pathfinding */
    public abstract boolean isDone(Vector3 vec);

    /** Called to execute the pathfinding operation. */
    public ResourcePathFinder start(final Vector3 startNode, int runCount, final boolean fill)
    {
        this.Start = startNode;
        this.fill = fill;
        this.runs = 0;
        this.resultsFound = 0;
        this.resultRun = runCount;
        this.find(ForgeDirection.UNKNOWN, startNode);

        this.refresh();
        this.sortResults(Start, results);
        return this;
    }

    public ResourcePathFinder reset()
    {
        this.nodeList.clear();
        this.results.clear();
        return this;
    }

    public ResourcePathFinder refresh()
    {
        Iterator<Vector3> it = this.nodeList.iterator();
        while (it.hasNext())
        {
            Vector3 vec = it.next();
            if (!this.isValidNode(vec))
            {
                it.remove();
            }
            if (this.isValidResult(vec))
            {
                this.addResult(vec);
            }
        }
        it = this.results.iterator();
        while (it.hasNext())
        {
            Vector3 vec = it.next();
            if (!this.isValidResult(vec))
            {
                it.remove();
            }
            if (this.isValidNode(vec))
            {
                this.addNode(vec);
            }
        }
        return this;
    }

    public void sortResults(Vector3 start, Set<Vector3> list)
    {

    }
}
