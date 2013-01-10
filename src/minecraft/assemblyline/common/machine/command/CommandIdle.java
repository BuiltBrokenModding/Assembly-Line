package assemblyline.common.machine.command;

import assemblyline.common.machine.armbot.TileEntityArmbot;


public class CommandIdle extends Command
{
	public static final float IDLE_ROTATION_PITCH = 0;
	public static final float IDLE_ROTATION_YAW = 0;

	public CommandIdle(TileEntityArmbot arm)
	{
		super(arm);
	}

	protected boolean doTask()
	{
		/**
		 * Move the arm rotation to idle position if the machine is not idling
		 */
		if (Math.abs(this.tileEntity.rotationPitch - IDLE_ROTATION_PITCH) > 0.001 || Math.abs(this.tileEntity.rotationYaw - IDLE_ROTATION_YAW) > 0.001)
		{
			if (Math.abs(IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) > 0.125)
				this.tileEntity.rotationPitch += (IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) * 0.05;
			else
				this.tileEntity.rotationPitch += Math.signum(IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) * (0.125 * 0.05);
			if (Math.abs(this.tileEntity.rotationPitch - IDLE_ROTATION_PITCH) < 0.0125)
				this.tileEntity.rotationPitch = IDLE_ROTATION_PITCH;
			
			if (Math.abs(IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) > 0.125)
				this.tileEntity.rotationYaw += (IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) * 0.05;
			else
				this.tileEntity.rotationYaw += Math.signum(IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) * (0.125 * 0.05);
			if (Math.abs(this.tileEntity.rotationYaw - IDLE_ROTATION_YAW) < 0.0125)
				this.tileEntity.rotationYaw = IDLE_ROTATION_YAW;
			
			return true;
		}

		/**
		 * Randomly move the arm to simulate life in the arm if the arm is powered
		 */
		//this.tileEntity.rotationYaw *= 0.98 * this.world.rand.nextFloat();
		return false;
	}

}
