package assemblyline.common.machine.command;

public class CommandReturn extends CommandRotate
{
	public static final float IDLE_ROTATION_PITCH = 0;
	public static final float IDLE_ROTATION_YAW = 0;

	@Override
	public void onTaskStart()
	{
		this.targetRotationYaw = IDLE_ROTATION_YAW;
		this.targetRotationPitch = IDLE_ROTATION_PITCH;
		float totalTicksYaw = Math.abs(this.targetRotationYaw - this.tileEntity.rotationYaw) / this.tileEntity.ROTATION_SPEED;
		float totalTicksPitch = Math.abs(this.targetRotationPitch - this.tileEntity.rotationPitch) / this.tileEntity.ROTATION_SPEED;
		this.totalTicks = Math.max(totalTicksYaw, totalTicksPitch);
	}

	/*
	 * @Override protected boolean doTask() { /** Move the arm rotation to idle position if the
	 * machine is not idling
	 * 
	 * if (Math.abs(this.tileEntity.rotationPitch - IDLE_ROTATION_PITCH) > 0.01 ||
	 * Math.abs(this.tileEntity.rotationYaw - IDLE_ROTATION_YAW) > 0.01) { if
	 * (Math.abs(IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) > 0.125)
	 * this.tileEntity.rotationPitch += (IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) *
	 * 0.05; else this.tileEntity.rotationPitch += Math.signum(IDLE_ROTATION_PITCH -
	 * this.tileEntity.rotationPitch) * (0.125 * 0.05); if (Math.abs(this.tileEntity.rotationPitch -
	 * IDLE_ROTATION_PITCH) < 0.0125) this.tileEntity.rotationPitch = IDLE_ROTATION_PITCH;
	 * 
	 * if (Math.abs(IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) > 0.125)
	 * this.tileEntity.rotationYaw += (IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) * 0.05; else
	 * this.tileEntity.rotationYaw += Math.signum(IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) *
	 * (0.125 * 0.05); if (Math.abs(this.tileEntity.rotationYaw - IDLE_ROTATION_YAW) < 0.0125)
	 * this.tileEntity.rotationYaw = IDLE_ROTATION_YAW; return true; }
	 * 
	 * return false; }
	 */

}
