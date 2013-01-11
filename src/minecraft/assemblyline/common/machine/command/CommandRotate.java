package assemblyline.common.machine.command;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

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
		if (this.getArg(0) == null)
		{
			this.targetRotation = this.tileEntity.rotationYaw + 90;
		}
		else
		{
			this.targetRotation = this.tileEntity.rotationYaw + this.getIntArg(0);
		}

		while (this.targetRotation > 360)
		{
			this.targetRotation -= 360;
		}
		while (this.targetRotation < -360)
		{
			this.targetRotation += 360;
		}
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (Math.abs(this.targetRotation - this.tileEntity.rotationYaw) > 0.125)
		{
			if (this.tileEntity.rotationYaw > this.targetRotation)
			{
				this.tileEntity.rotationYaw -= ROTATION_SPEED;
			}
			else
			{
				this.tileEntity.rotationYaw += ROTATION_SPEED;
			}

			// System.out.println("[" + ((FMLCommonHandler.instance().getEffectiveSide() ==
			// Side.SERVER) ? "S" : "C") + "]" + "Target: " + this.targetRotation + "; current: " +
			// this.tileEntity.rotationYaw);

			if (Math.abs(this.tileEntity.rotationYaw - this.targetRotation) < 0.5)
				this.tileEntity.rotationYaw = this.targetRotation;

			return true;
		}
		if (ticks < 80) // wait for a few ticks after rotating
			return true;

		return false;
	}
}
