package assemblyline.api;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * An interface applied to Armbots.
 * 
 * @author Calclavia
 */
public interface IArmbot
{
	/**
	 * Adds an entity to the Armbot's grab list.
	 */
	public void grabEntity(Entity entity);

	public void grabItem(ItemStack itemStack);

	/**
	 * Drops a specific entity from the Armbot's hand.
	 */
	public void dropEntity(Entity entity);

	public void dropItem(ItemStack itemStack);

	/**
	 * Drops all entities in the Armbot's hand.
	 */
	public void dropAll();

	/**
	 * @return Returns all entities being grabbed by the Armbot.
	 */
	public List<Entity> getGrabbedEntities();

	public List<ItemStack> getGrabbedItems();
}
