package assemblyline.common.machine.crafter;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import assemblyline.common.ai.Task;

/**
 * Used by arms to search for entities in a region
 * 
 * @author Calclavia
 */
public class TaskArmSearch extends Task
{

	/**
	 * The item to be collected.
	 */
	private Class<? extends Entity> entityToInclude;

	private float searchSpeed;

	private double radius;

	private Entity foundEntity;

	public TaskArmSearch(TileEntityArmbot arm, Class<? extends Entity> entityToInclude, double radius, float searchSpeed)
	{
		super(arm);
		this.entityToInclude = entityToInclude;
		this.radius = radius;
		this.searchSpeed = searchSpeed;
	}

	@Override
	public void onTaskStart()
	{
		List found = tileEntity.worldObj.getEntitiesWithinAABB(entityToInclude, AxisAlignedBB.getBoundingBox(tileEntity.xCoord - radius, tileEntity.yCoord - radius, tileEntity.zCoord - radius, tileEntity.xCoord + radius, tileEntity.yCoord + radius, tileEntity.zCoord + radius));
		if (found != null && !found.isEmpty())
		{
			foundEntity = (Entity) found.get(0);
		}
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.entityToInclude == null || this.foundEntity == null) { return false; }

		/**
		 * Move the robotic arm around and emulate an item search. Then initiate a collect task.
		 */

		return true;
	}
}
