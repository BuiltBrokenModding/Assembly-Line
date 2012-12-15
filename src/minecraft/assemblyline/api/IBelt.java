package assemblyline.api;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.ForgeDirection;

/**
 * An interface applied to the tile entity of a conveyor belt.
 * @author Calclavia
 *
 */
public interface IBelt
{
	/**
	 * Used to get a list of entities the belt exerts an effect upon.
	 * 
	 * @return list of entities in the belts are of effect
	 */
	public List<Entity> getAffectedEntities();
}
