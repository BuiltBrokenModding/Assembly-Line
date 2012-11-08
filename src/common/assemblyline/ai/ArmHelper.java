package assemblyline.ai;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import universalelectricity.core.Vector3;

public class ArmHelper
{

	/**
	 * Used to locate items in an area
	 * 
	 * @param start
	 *            - start xyz
	 * @param End
	 *            - end xyz
	 * @return list of items
	 */
	public List<EntityItem> findItems(World world, Vector3 start, Vector3 end)
	{
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(start.x, start.y, start.z, end.x, end.x, end.x);
		// EntityItem list
		List<EntityItem> itemsList = world.getEntitiesWithinAABB(EntityItem.class, bounds);
		return itemsList;
	}

	/**
	 * Used to locate an item type in an area
	 * 
	 * @param world
	 * @param start
	 * @param end
	 * @param item
	 * @return list of matching items
	 */
	public List<EntityItem> findItems(World world, Vector3 start, Vector3 end, ItemStack stack)
	{
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(start.x, start.y, start.z, end.x, end.x, end.x);
		// EntityItem list
		List<EntityItem> itemsList = world.getEntitiesWithinAABB(EntityItem.class, bounds);
		for (EntityItem item : itemsList)
		{
			ItemStack stackItem = item.item;
			if (stackItem.itemID != stack.itemID || stackItem.getItemDamage() != stack.getItemDamage())
			{
				itemsList.remove(item);
			}
		}
		return itemsList;
	}
}
