package assemblyline.common.machine.crane;

import assemblyline.common.AssemblyLine;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
/**
 * Manager of crane movement, mapping, setup, but not AI
 * @author Rseifert
 *
 */
public class CraneManager
{
	protected int ticks = 0;
	/** Limits of how far the crane can move on a 2D plane */
	public int maxLimitX, minLimitY,maxLimitZ, minLimitZ;

	public World world;

	public CraneManager()
	{

	}
	/**
	 * maps out the rail system that the crane will use for movement
	 */
	public void mapRails()
	{
		//TODO figure out what i'm going to use as the center point of this mapping first
	}
	/**
	 * finds if the rail at this location is a rail and lines up with the current direction of the rails
	 * 
	 * @return true if the rail is good to use
	 */
	public boolean isValidRail(World world, Vector3 loc, ForgeDirection dir)
	{
		int id = world.getBlockId(loc.intX(), loc.intY(), loc.intZ());
		int meta = world.getBlockMetadata(loc.intX(), loc.intY(), loc.intZ());
		ForgeDirection facing = ForgeDirection.getOrientation(meta);

		if (id == AssemblyLine.blockCraneParts.blockID && (dir == facing || dir == facing.getOpposite()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
