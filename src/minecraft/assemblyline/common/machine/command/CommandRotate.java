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

	public CommandRotate(TileEntityArmbot arm, String...parameters)
	{
		super(arm, parameters);
		this.targetRotation = arm.rotationYaw + (float) (Math.PI / 2);
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.tileEntity.rotationYaw != this.targetRotation)
		{
			if (Math.abs(this.targetRotation - this.tileEntity.rotationYaw) > 0.125)
				this.tileEntity.rotationYaw += (this.targetRotation - this.tileEntity.rotationYaw) * 0.05;
			else
				this.tileEntity.rotationYaw += Math.signum(this.targetRotation - this.tileEntity.rotationYaw) * (0.125 * 0.05);
			if (Math.abs(this.tileEntity.rotationYaw - this.targetRotation) < 0.0125)
				this.tileEntity.rotationYaw = this.targetRotation;
			return true;
		}
		if (ticks < 80)
			return true;

		return false;
	}
}
