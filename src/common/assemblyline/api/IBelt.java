package assemblyline.api;

import java.util.List;

import net.minecraft.src.Entity;
import net.minecraftforge.common.ForgeDirection;

public interface IBelt
{
	/**
	 * Gets the facing direction of the belt, used but other machines to know which direction to
	 * start an item at
	 * 
	 * @return
	 */
	public ForgeDirection getFacing();

	/**
	 * Causes the belt to ignore the entity for a few updates help in cases where another machine
	 * needs to affect this particular entity without the belt interfering
	 * 
	 * @param entity
	 *            - entity being ignored
	 */
	public void ignoreEntity(Entity entity);

	/**
	 * Used to get a list of entities the belt exerts an effect upon.
	 * 
	 * @return list of entities in the belts are of effect
	 */
	public List<Entity> getAffectedEntities();
}
