package hydraulic.core.path;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

/**
 * Check if a conductor connects with another.
 * 
 * @author Calclavia, DarkGuardsman
 * 
 */
public class PathfinderChecker extends Pathfinder
{
	List<Vector3> ignoreList;
	
	public PathfinderChecker(World world, Vector3 targetConnector, List<Integer> blockIds, Vector3... ignoreConnector)
	{
		super(world, blockIds);
		this.ignoreList = Arrays.asList(ignoreConnector);
		this.target = targetConnector;
	}

	@Override
	public boolean isValidNode(ForgeDirection direction, Vector3 connectedBlock)
	{
		return !ignoreList.contains(connectedBlock);
	}
}
