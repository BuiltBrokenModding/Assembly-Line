package assemblyline.common.machine.command;


/**
 * Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author Calclavia
 */
public class CommandRotate extends Command
{
	public static final float ROTATION_SPEED = 1.3f;
	float targetRotation = 0;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();

		if (this.getArg(0) == null)
		{
			this.targetRotation = this.tileEntity.rotationYaw + 90;
		}
		else
		{
			this.targetRotation = this.tileEntity.rotationYaw + this.getIntArg(0);
		}

		while (this.targetRotation >= 360)
		{
			this.targetRotation -= 360;
		}
		while (this.targetRotation <= -360)
		{
			this.targetRotation += 360;
		}
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();
		float rotationalDifference = Math.abs(this.tileEntity.rotationYaw - this.targetRotation);

		if (rotationalDifference < 0.1)
		{
			this.tileEntity.rotationYaw = this.targetRotation;
		}
		else
		{
			if (this.tileEntity.rotationYaw > this.targetRotation)
			{
				this.tileEntity.rotationYaw -= ROTATION_SPEED;
			}
			else
			{
				this.tileEntity.rotationYaw += ROTATION_SPEED;
			}
		}

		if (this.ticks < 80) { return true; }

		return false;
	}
}
