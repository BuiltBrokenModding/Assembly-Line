package assemblyline.common.machine.command;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author Calclavia
 */
public class CommandRotateTo extends Command
{
	float	targetRotationYaw	= 0;
	float	targetRotationPitch	= 0;
	int		totalTicks			= 0;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();

		this.ticks = 0;
		this.totalTicks = 0;

		if (this.getArg(0) != null)
		{
			this.targetRotationYaw = this.getFloatArg(0);
		}
		else
		{
			this.targetRotationYaw = 0;
		}

		if (this.getArg(1) != null)
		{
			this.targetRotationPitch = this.getFloatArg(1);
		}
		else
		{
			this.targetRotationPitch = 0;
		}

		this.targetRotationYaw = this.targetRotationYaw % 360;
		this.targetRotationPitch = this.targetRotationPitch % 60;

		int totalTicksYaw = (int) (Math.abs(this.targetRotationYaw - this.tileEntity.renderYaw) / this.tileEntity.ROTATION_SPEED);
		int totalTicksPitch = (int) (Math.abs(this.targetRotationPitch - this.tileEntity.renderPitch) / this.tileEntity.ROTATION_SPEED);
		this.totalTicks = (int) Math.max(totalTicksYaw, totalTicksPitch);
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();
		/*
		 * float rotationalDifference = Math.abs(this.tileEntity.rotationYaw - this.targetRotation);
		 * 
		 * if (rotationalDifference < ROTATION_SPEED) { this.tileEntity.rotationYaw = this.targetRotation; } else { if (this.tileEntity.rotationYaw > this.targetRotation) { this.tileEntity.rotationYaw -= ROTATION_SPEED; } else { this.tileEntity.rotationYaw += ROTATION_SPEED; } this.ticks = 0; }
		 */

		// set the rotation to the target immediately and let the client handle animating it
		// wait for the client to catch up

		if (Math.abs(this.tileEntity.rotationYaw - this.targetRotationYaw) > 0.001f)
			this.tileEntity.rotationYaw = this.targetRotationYaw;
		if (Math.abs(this.tileEntity.rotationPitch - this.targetRotationPitch) > 0.001f)
			this.tileEntity.rotationPitch = this.targetRotationPitch;

		if (this.ticks < this.totalTicks) { return true; }

		return false;
	}

	@Override
	public String toString()
	{
		return "ROTATETO " + Float.toString(this.targetRotationYaw) + " " + Float.toString(this.targetRotationPitch);
	}

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.targetRotationPitch = taskCompound.getFloat("rotPitch");
		this.targetRotationYaw = taskCompound.getFloat("rotYaw");
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		taskCompound.setFloat("rotPitch", this.targetRotationPitch);
		taskCompound.setFloat("rotYaw", this.targetRotationYaw);
	}
}
