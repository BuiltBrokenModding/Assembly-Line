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
	public static final float ROTATION_SPEED = 1f;
	float targetRotation = 0;

	@Override
	public void onTaskStart()
	{
		this.targetRotation = this.tileEntity.rotationYaw + 90;

		while (this.targetRotation > 360)
		{
			this.targetRotation -= 360;
		}
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.tileEntity.rotationYaw > this.targetRotation)
		{
			this.tileEntity.rotationYaw -= ROTATION_SPEED;
		}
		else
		{
			this.tileEntity.rotationYaw += ROTATION_SPEED;
		}

		if (Math.abs(this.targetRotation - this.tileEntity.rotationYaw) < 0.125) { return false; }

		return true;
	}
}
