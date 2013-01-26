package assemblyline.common.machine.command;

public class CommandReturn extends Command
{
	public static final float	IDLE_ROTATION_PITCH	= 0;
	public static final float	IDLE_ROTATION_YAW	= 0;

	private CommandRotateTo		rotateToCommand;

	@Override
	public void onTaskStart()
	{
		this.rotateToCommand = (CommandRotateTo) this.commandManager.getNewCommand(this.tileEntity, CommandRotateTo.class, new String[] { "0", "0" });
		this.rotateToCommand.onTaskStart();
	}
	
	@Override
	protected boolean doTask()
	{
		return this.rotateToCommand.doTask();
	}
	
	@Override
	public void onTaskEnd()
	{
		this.rotateToCommand.onTaskEnd();
	}

	/*
	 * @Override protected boolean doTask() { /** Move the arm rotation to idle position if the machine is not idling
	 * 
	 * if (Math.abs(this.tileEntity.rotationPitch - IDLE_ROTATION_PITCH) > 0.01 || Math.abs(this.tileEntity.rotationYaw - IDLE_ROTATION_YAW) > 0.01) { if (Math.abs(IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) > 0.125) this.tileEntity.rotationPitch += (IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) * 0.05; else this.tileEntity.rotationPitch += Math.signum(IDLE_ROTATION_PITCH - this.tileEntity.rotationPitch) * (0.125 * 0.05); if (Math.abs(this.tileEntity.rotationPitch - IDLE_ROTATION_PITCH) < 0.0125) this.tileEntity.rotationPitch = IDLE_ROTATION_PITCH;
	 * 
	 * if (Math.abs(IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) > 0.125) this.tileEntity.rotationYaw += (IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) * 0.05; else this.tileEntity.rotationYaw += Math.signum(IDLE_ROTATION_YAW - this.tileEntity.rotationYaw) * (0.125 * 0.05); if (Math.abs(this.tileEntity.rotationYaw - IDLE_ROTATION_YAW) < 0.0125) this.tileEntity.rotationYaw = IDLE_ROTATION_YAW; return true; }
	 * 
	 * return false; }
	 */
	
	@Override
	public String toString()
	{
		return "RETURN";
	}

}
