package assemblyline.common.machine.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

/**
 * Used by arms to search for entities in a region
 * 
 * @author Calclavia
 */
public class CommandGrab extends Command
{

	public static final float radius = 0.5f;

	/**
	 * The item to be collected.
	 */
	private Class<? extends Entity> entityToInclude;

	public CommandGrab()
	{
		super();
		this.entityToInclude = Entity.class;
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		Vector3 serachPosition = this.tileEntity.getHandPosition();
		List<Entity> found = this.world.getEntitiesWithinAABB(this.entityToInclude, AxisAlignedBB.getBoundingBox(serachPosition.x - radius, serachPosition.y - radius, serachPosition.z - radius, serachPosition.x + radius, serachPosition.y + radius, serachPosition.z + radius));

		if (found != null && found.size() > 0)
		{
			this.tileEntity.grabbedEntities.add(found.get(0));
			return false;
		}
		/**
		 * Move the robotic arm around and emulate an item search. Then initiate a collect task.
		 */

		return true;
	}
}
