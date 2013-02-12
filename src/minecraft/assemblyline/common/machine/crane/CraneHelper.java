package assemblyline.common.machine.crane;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import assemblyline.api.ICraneConnectable;
import assemblyline.api.ICraneStructure;

/**
 * Manager of crane movement, mapping, setup, but not AI
 * 
 * @author Rseifert
 * 
 */
public class CraneHelper
{
	/**
	 * The maximum size that a crane can be
	 */
	public static final int MAX_SIZE = 64;
	
	public static boolean isCraneBlock(World world, int x, int y, int z)
	{
		return world.getBlockTileEntity(x, y, z) != null && world.getBlockTileEntity(x, y, z) instanceof ICraneConnectable;
	}
	
	public static boolean isCraneStructureBlock(World world, int x, int y, int z)
	{
		return world.getBlockTileEntity(x, y, z) != null && world.getBlockTileEntity(x, y, z) instanceof ICraneStructure;
	}

	public static boolean canFrameConnectTo(TileEntity tileEntity, int x, int y, int z, ForgeDirection side)
	{
		if (tileEntity.worldObj.getBlockTileEntity(x, y, z) != null && tileEntity.worldObj.getBlockTileEntity(x, y, z) instanceof ICraneConnectable)
		{
			return ((ICraneConnectable) tileEntity.worldObj.getBlockTileEntity(x, y, z)).canFrameConnectTo(side);
		}

		return false;
	}
	
	public static ForgeDirection rotateClockwise(ForgeDirection direction)
	{
		if (direction == ForgeDirection.NORTH)
			return ForgeDirection.EAST;
		if (direction == ForgeDirection.EAST)
			return ForgeDirection.SOUTH;
		if (direction == ForgeDirection.SOUTH)
			return ForgeDirection.WEST;
		if (direction == ForgeDirection.WEST)
			return ForgeDirection.NORTH;
		return ForgeDirection.UNKNOWN;
	}
	
	public static ForgeDirection rotateCounterClockwise(ForgeDirection direction)
	{
		if (direction == ForgeDirection.NORTH)
			return ForgeDirection.WEST;
		if (direction == ForgeDirection.WEST)
			return ForgeDirection.SOUTH;
		if (direction == ForgeDirection.SOUTH)
			return ForgeDirection.EAST;
		if (direction == ForgeDirection.EAST)
			return ForgeDirection.NORTH;
		return ForgeDirection.UNKNOWN;
	}
}
