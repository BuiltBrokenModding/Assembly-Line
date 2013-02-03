package assemblyline.api;

import java.util.List;

import net.minecraft.entity.Entity;

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

	/**
	 * Drops a specific entity from the Armbot's hand.
	 */
	public void dropEntity(Entity entity);

	/**
	 * Drops all entities in the Armbot's hand.
	 */
	public void dropAll();

	/**
	 * @return Returns all entities being grabbed by the Armbot.
	 */
	public List<Entity> getGrabbedEntities();
}
