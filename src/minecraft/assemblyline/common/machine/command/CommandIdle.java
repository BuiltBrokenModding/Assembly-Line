package assemblyline.common.machine.command;

import assemblyline.common.machine.armbot.TileEntityArmbot;

public class CommandIdle extends Command
{
	/**
	 * The amount of time in which the machine will idle.
	 */
	public int idleTime = 80;

	public void onTaskStart()
	{
		super.onTaskStart();

		if (this.getIntArg(0) > 0)
		{
			this.idleTime = this.getIntArg(0);
		}
		
		System.out.println("INITIATE");
	}

	protected boolean doTask()
	{
		/**
		 * Randomly move the arm to simulate life in the arm if the arm is powered
		 */
		this.tileEntity.rotationPitch *= 0.98 * this.world.rand.nextFloat();

		if (this.idleTime > 0)
		{
			this.idleTime--;
			return true;
		}

		return false;
	}

}
