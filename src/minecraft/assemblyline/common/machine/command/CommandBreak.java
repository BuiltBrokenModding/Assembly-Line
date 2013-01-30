package assemblyline.common.machine.command;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import universalelectricity.core.vector.Vector3;

/**
 * Used by arms to break a specific block in a position.
 * 
 * @author Calclavia
 */
public class CommandBreak extends Command
{
	public static final float radius = 0.5f;

	public CommandBreak()
	{
		super();
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.tileEntity.grabbedEntities.size() > 0)
			return false;

		Vector3 serachPosition = this.tileEntity.getHandPosition();

		Block block = Block.blocksList[serachPosition.getBlockID(this.world)];

		if (block != null)
		{
			block.dropBlockAsItem(this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), serachPosition.getBlockMetadata(this.world), 0);
			serachPosition.setBlockWithNotify(this.world, 0);
		}

		return true;
	}

	@Override
	public String toString()
	{
		return "BREAK";
	}
}
