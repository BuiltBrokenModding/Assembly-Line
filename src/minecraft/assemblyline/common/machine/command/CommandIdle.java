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
		if (this.tileEntity.rotationPitch != IDLE_ROTATION_PITCH && this.tileEntity.rotationYaw != IDLE_ROTATION_YAW)
		{
			this.tileEntity.rotationPitch += (IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) * 0.05;
			this.tileEntity.rotationYaw += (IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) * 0.05;
			return true;
		}

		/**
		 * Randomly move the arm to simulate life in the arm if the arm is powered
		 */
		this.tileEntity.rotationYaw *= 0.98 * this.world.rand.nextFloat();
		return false;
	}

}
