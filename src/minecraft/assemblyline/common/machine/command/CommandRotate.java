package assemblyline.common.machine.command;


/**
 * Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author Calclavia
 */
public class CommandRotate extends Command
{
	float targetRotation = 0;
	float totalTicks = 0f;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();
		
		this.ticks = 0;

		if (this.getArg(0) == null)
		{
			this.targetRotation = this.tileEntity.rotationYaw + 90;
		}
		else
		{
			this.targetRotation = this.tileEntity.rotationYaw + this.getFloatArg(0);
		}

		while (this.targetRotation >= 360)
		{
			this.targetRotation -= 360;
		}
		while (this.targetRotation <= -360)
		{
			this.targetRotation += 360;
		}
		
		this.totalTicks = Math.abs(this.targetRotation - this.tileEntity.rotationYaw) / this.tileEntity.ROTATION_SPEED;
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();
		/*float rotationalDifference = Math.abs(this.tileEntity.rotationYaw - this.targetRotation);

		if (rotationalDifference < ROTATION_SPEED)
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
			this.ticks = 0;
		}*/
		
		//set the rotation to the target immediately and let the client handle animating it
		//wait for the client to catch up
		if (Math.abs(this.tileEntity.rotationYaw - this.targetRotation) > 0.001f)
			this.tileEntity.rotationYaw = this.targetRotation;

		if (this.ticks < this.totalTicks)
		{
			return true;
		}

		return false;
	}
}
