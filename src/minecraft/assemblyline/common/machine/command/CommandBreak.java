package assemblyline.common.machine.command;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import universalelectricity.core.vector.Vector3;
import dark.minecraft.helpers.ItemWorldHelper;

/**
 * Used by arms to break a specific block in a position.
 * 
 * @author Calclavia
 */
public class CommandBreak extends Command
{
	private CommandRotateTo rotateToCommand;

	int BREAK_TIME = 30;
	boolean keep = false;

	@Override
	protected boolean doTask()
	{
		super.doTask();

		Vector3 serachPosition = this.tileEntity.getHandPosition();

		Block block = Block.blocksList[serachPosition.getBlockID(this.world)];

		if (block != null && BREAK_TIME <= this.ticks)
		{
			ArrayList<ItemStack> items = block.getBlockDropped(this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), serachPosition.getBlockMetadata(world), 0);

			if (!this.keep || items.size() > 1)
			{
				ItemWorldHelper.dropBlockAsItem(this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ());
			}
			else
			{
				this.tileEntity.grabEntity(new EntityItem(this.world, (double) serachPosition.intX() + 0.5D, (double) serachPosition.intY() + 0.5D, (double) serachPosition.intZ() + 0.5D, items.get(0)));
			}

			serachPosition.setBlockWithNotify(this.world, 0);
			return false;
		}

		/**
		 * Notes on break command Beds Break Wrong Multi blocks don't work
		 */
		return true;
	}

	

	@Override
	public String toString()
	{
		return "BREAK";
	}
}
