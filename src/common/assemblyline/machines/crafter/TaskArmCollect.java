package assemblyline.machines.crafter;

import net.minecraft.src.EntityItem;
import net.minecraft.src.TileEntity;
import assemblyline.ai.Task;

/**
 * Used by arms to collect items in a specific
 * region.
 * 
 * @author Calclavia
 */
public class TaskArmCollect extends Task
{
	

	/**
	 * The item to be collected.
	 */
	private EntityItem entityItem;

	public TaskArmCollect(TileEntityCraftingArm arm,EntityItem entityItem)
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
		 * Slowly stretch down the arm's model and
		 * grab the item
		 */

		return true;
	}
}
