package dark.library.machine;

import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.TileEntityUniversalRunnable;
import dark.library.PowerSystems;

public class TileEntityRunnableMachine extends TileEntityUniversalRunnable
{

	/** The amount of players using the console. */
	public int playersUsing = 0;

	@Override
	public void updateEntity()
	{
		if (this.wattsReceived < this.getWattBuffer() && PowerSystems.runPowerLess(PowerSystems.INDUSTRIALCRAFT, PowerSystems.BUILDCRAFT, PowerSystems.MEKANISM))
		{
			this.wattsReceived += Math.max(this.getWattBuffer() - this.wattsReceived, 0);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.wattsReceived = nbt.getDouble("wattsReceived");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("wattsReceived", this.wattsReceived);
	}
}
