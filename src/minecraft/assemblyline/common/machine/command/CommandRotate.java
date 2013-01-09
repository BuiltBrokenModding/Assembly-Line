package assemblyline.common.machine.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import assemblyline.common.machine.armbot.TileEntityArmbot;

/**
 * Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author Calclavia
 */
public class CommandRotate extends Command
{
	float targetRotation = 0;

	public CommandRotate(TileEntityArmbot arm, String[] parameters)
	{
		super(arm, parameters);
		this.targetRotation = arm.rotationPitch + 90;
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.tileEntity.rotationPitch < this.targetRotation)
		{
			this.tileEntity.rotationPitch += 0.01;
			return true;
		}

		return false;
	}
}
