package assemblyline.common.machine.command;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import universalelectricity.core.vector.Vector3;

/**
 * Used by arms to break a specific block in a position.
 * 
 * @author Calclavia
 */
public class CommandHarvest extends CommandBreak
{
	private CommandRotateTo rotateToCommand;
	@Override
	public void onTaskStart()
	{
		this.keep = true;
	}

	@Override
	public String toString()
	{
		return "HARVEST";
	}
}
