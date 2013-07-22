package dark.assembly.api;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/** Should be used to interact with the armbot and not to create a new armbot */
public interface IArmbot
{
	/** Adds an entity to the Armbot's grab list. */
	public void grabEntity(Entity entity);

	public void grabItem(ItemStack itemStack);

	/** Drops the given object
	 * 
	 * @param object - Entity or ItemStack
	 * 
	 * String "All" should cause the armbot to drop all items */
	public void drop(Object object);

	/** @return Returns all entities being grabbed by the Armbot. */
	public List<Entity> getGrabbedEntities();

	public List<ItemStack> getGrabbedItems();
}
