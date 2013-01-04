package assemblyline.common.machine.crafter;

import net.minecraft.entity.item.EntityItem;
import assemblyline.common.machine.armbot.Command;

/**
 * Used by arms to collect items in a specific region.
 * 
 * @author Calclavia
 */
public class TaskArmCollect extends Command
{

	/**
	 * The item to be collected.
	 */
	private EntityItem entityItem;

	public TaskArmCollect(TileEntityArmbot arm, EntityItem entityItem)
	{
		super(arm);
		this.entityItem = entityItem;
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (entityItem == null) { return false; }

		/**
		 * Slowly stretch down the arm's model and grab the item
		 */

		return true;
	}
}
