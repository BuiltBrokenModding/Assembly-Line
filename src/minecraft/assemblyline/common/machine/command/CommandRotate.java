package assemblyline.common.machine.command;


/**
 * Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author Calclavia
 */
public class CommandRotate extends Command
{
	float targetRotationYaw = 0;
	float targetRotationPitch = 0;
	float totalTicks = 0f;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();
		
		this.ticks = 0;

		if (this.getArg(0) != null)
		{
			this.targetRotationYaw = this.tileEntity.rotationYaw + this.getFloatArg(0);
		}
		else
		{
			this.targetRotationYaw = this.tileEntity.rotationYaw + 90;
		}
		
		if (this.getArg(1) != null)
		{
			this.targetRotationPitch = this.tileEntity.rotationPitch + this.getFloatArg(1);
		}
		else
		{
			this.targetRotationPitch = this.tileEntity.rotationPitch;
		}

		while (this.targetRotationYaw >= 360)
		{
			this.targetRotationYaw -= 360;
		}
		while (this.targetRotationYaw <= -360)
		{
			this.targetRotationYaw += 360;
		}
		
		if (this.targetRotationPitch >= 60)
		{
			this.targetRotationPitch = 60;
		}
		if (this.targetRotationPitch <= 0)
		{
			this.targetRotationPitch = 0;
		}
		
		float totalTicksYaw = Math.abs(this.targetRotationYaw - this.tileEntity.rotationYaw) / this.tileEntity.ROTATION_SPEED;
		float totalTicksPitch = Math.abs(this.targetRotationPitch - this.tileEntity.rotationPitch) / this.tileEntity.ROTATION_SPEED;
		this.totalTicks = Math.max(totalTicksYaw, totalTicksPitch);
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
		if (Math.abs(this.tileEntity.rotationYaw - this.targetRotationYaw) > 0.001f)
			this.tileEntity.rotationYaw = this.targetRotationYaw;
		if (Math.abs(this.tileEntity.rotationPitch - this.targetRotationPitch) > 0.001f)
			this.tileEntity.rotationPitch = this.targetRotationPitch;

		if (this.ticks < this.totalTicks)
		{
			return true;
		}

		return false;
	}
}
