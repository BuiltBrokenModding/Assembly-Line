package hydraulic.helpers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

/**
 * Used to help with draining and filling of a tank
 * 
 * @author DarkGuardsman
 */
public class FillDrainHelper
{
	/**
	 * Fills all ITankContainers around the point
	 * 
	 * @return amount filled into the tank, use this to drain the source of the stack
	 */
	public static int fillArround(World world, Vector3 center, LiquidStack stack)
	{
		if (stack == null || stack.amount <= 0 || center.y < 6 || center.y > 255)
		{
			return 0;
		}
		int fill = 0;
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
		{
			fill += FillDrainHelper.fillDirection(world, center, stack, direction);
		}
		return fill;
	}

	/**
	 * Fills a ITankContainer in one direction from a point in the world
	 * 
	 * @return amount filled into the tank, use this to drain the source of the stack
	 */
	public static int fillDirection(World world, Vector3 center, LiquidStack stack, ForgeDirection direction)
	{
		if (stack == null || stack.amount <= 0 || center.y < 6 || center.y > 255)
		{
			return 0;
		}
		TileEntity entity = VectorHelper.getTileEntityFromSide(world, center, direction);
		if (entity instanceof ITankContainer && ((ITankContainer) entity).fill(direction.getOpposite(), stack, false) > 0)
		{
			return ((ITankContainer) entity).fill(direction.getOpposite(), stack, true);
		}
		return 0;
	}
}
