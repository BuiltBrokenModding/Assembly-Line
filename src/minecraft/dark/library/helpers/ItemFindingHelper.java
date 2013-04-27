package dark.library.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ItemFindingHelper
{

	/**
	 * gets all EntityItems in a location using a start and end point
	 */
	public static List<EntityItem> findAllItemIn(World world, Vector3 start, Vector3 end)
	{
		return world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(start.x, start.y, start.z, end.x, end.y, end.z));
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
		List<EntityItem> entityItems = ItemFindingHelper.findAllItemIn(world, start, end);
		return filterEntityItemsList(entityItems, disiredItems);
	}

	/**
	 * filters an EntityItem List to a List of Items
	 */
	public static List<EntityItem> filterEntityItemsList(List<EntityItem> entityItems, List<ItemStack> disiredItems)
	{
		List<EntityItem> newItemList = new ArrayList<EntityItem>();

		for (EntityItem entityItem : entityItems)
		{
			for (ItemStack itemStack : disiredItems)
			{
				if (entityItem.getEntityItem().itemID == itemStack.itemID && entityItem.getEntityItem().getItemDamage() == itemStack.getItemDamage() && !newItemList.contains(entityItem))
				{
					newItemList.add(entityItem);
					break;
				}
			}
		}
		return newItemList;
	}

	/**
	 * filters out EnittyItems from an Entity list
	 */
	public static List<EntityItem> filterOutEntityItems(List<Entity> entities)
	{
		List<EntityItem> newEntityList = new ArrayList<EntityItem>();

		for (Entity entity : entities)
		{
			if (entity instanceof EntityItem)
			{
				newEntityList.add((EntityItem) entity);
			}

		}
		return newEntityList;
	}

	/**
	 * filter a list of itemStack to another list of itemStacks
	 * 
	 * @param totalItems - full list of items being filtered
	 * @param desiredItems - list the of item that are being filtered too
	 * @return a list of item from the original that are wanted
	 */
	public static List<ItemStack> filterItems(List<ItemStack> totalItems, List<ItemStack> desiredItems)
	{
		List<ItemStack> newItemList = new ArrayList<ItemStack>();

		for (ItemStack entityItem : totalItems)
		{
			for (ItemStack itemStack : desiredItems)
			{
				if (entityItem.itemID == itemStack.itemID && entityItem.getItemDamage() == itemStack.getItemDamage() && !newItemList.contains(entityItem))
				{
					newItemList.add(entityItem);
					break;
				}
			}
		}
		return newItemList;
	}

	/**
	 * Drops an item stack at the exact center of the location without any velocity or random throw
	 * angle
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
