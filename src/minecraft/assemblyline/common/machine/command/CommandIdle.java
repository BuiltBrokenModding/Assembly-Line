package assemblyline.common.machine.command;

import assemblyline.common.machine.armbot.TileEntityArmbot;

public class CommandIdle extends Command
{
	protected boolean doTask()
	{
		/**
		 * Randomly move the arm to simulate life in the arm if the arm is powered
		 */
		this.tileEntity.rotationPitch *= 0.98 * this.world.rand.nextFloat();
		return false;
	}

}
