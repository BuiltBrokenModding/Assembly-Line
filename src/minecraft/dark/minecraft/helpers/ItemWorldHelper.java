package dark.minecraft.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ItemWorldHelper
{

	/**
	 * gets all EntityItems in a location using a start and end point
	 */
	public static List<EntityItem> findAllItemIn(World world, Vector3 start, Vector3 end)
	{
		return world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(start.x, start.y, start.z, end.x, end.x, end.x));
	}

	/**
	 * Gets all EntityItems in an area and sorts them by a list of itemStacks
	 * 
	 * @param world - world being worked in
	 * @param start - start point
	 * @param end - end point
	 * @param disiredItems - list of item that are being looked for
	 * @return a list of EntityItem that match the itemStacks desired
	 */
	public static List<EntityItem> findSelectItems(World world, Vector3 start, Vector3 end, List<ItemStack> disiredItems)
	{
		List<EntityItem> entityItems = ItemWorldHelper.findAllItemIn(world, start, end);
		List<EntityItem> newItemList = new ArrayList<EntityItem>();

		for (EntityItem entityItem : entityItems)
		{
			for (ItemStack itemStack : disiredItems)
			{
				if (entityItem.getEntityItem().itemID == itemStack.itemID && entityItem.getEntityItem().getItemDamage() == itemStack.getItemDamage() && !newItemList.contains(entityItem))
				{
					entityItems.add(entityItem);
					break;
				}
			}
		}
		return newItemList;
	}

	/**
	 * Drops an item stack at the exact center of the location without any velocity or random throw angle
	 * 
	 * @param world - world to drop the item in
	 * @param x y z - location vector
	 * @param stack - itemstack to drop
	 * @return if the item was spawned in the world
	 */
	public static boolean dropItemStackExact(World world, double x, double y, double z, ItemStack stack)
	{
		if (!world.isRemote && stack != null)
		{
			EntityItem entity = new EntityItem(world, x, y, z, stack);
			entity.delayBeforeCanPickup = 10;
			return world.spawnEntityInWorld(entity);
		}
		return false;
	}

	/**
	 * grabs all the items that the block can drop then pass them onto dropBlockAsItem_do
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void dropBlockAsItem(World world, int x, int y, int z)
	{
		if (!world.isRemote)
		{
			int meta = world.getBlockMetadata(x, y, z);
			int id = world.getBlockId(x, y, z);
			ArrayList<ItemStack> items = Block.blocksList[id].getBlockDropped(world, x, y, z, meta, 0);

			for (ItemStack item : items)
			{
				dropItemStackExact(world, x + .5, y + .5, z + .5, item);
			}
		}
	}
}
