package assemblyline.api;

import net.minecraft.src.ItemStack;
import universalelectricity.core.vector.Vector3;

public interface IManipulator
{
	/**
	 * Throws the items from the manipulator into
	 * the world
	 * 
	 * @param outputPosition
	 * @param items
	 */
	public void rejectItem(Vector3 outputPosition, ItemStack items);

	// TODO add a few more methods here to access
	// the functions the manipulator
	// can do. For example storing items, and
	// retrieving items, or power on/off
}
