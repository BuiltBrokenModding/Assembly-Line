package assemblyline.common.machine.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class CommandDrop extends Command
{
	@Override
	protected boolean doTask()
	{
		super.doTask();

		// TODO: Animate Armbot to move down and drop all items.
		this.tileEntity.grabbedEntities.clear();
		return false;
	}
}
