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
	private TileEntityCraftingArm tileEntity;

	/**
	 * The item to be collected.
	 */
	private EntityItem entityItem;

	public TaskArmCollect(EntityItem entityItem)
	{
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

	@Override
	public void setTileEntity(TileEntity tileEntity)
	{
		this.tileEntity = (TileEntityCraftingArm) tileEntity;
	}
}
