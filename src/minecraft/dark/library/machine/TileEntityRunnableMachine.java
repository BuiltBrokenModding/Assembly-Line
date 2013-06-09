package dark.library.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import calclavia.lib.TileEntityUniversalRunnable;
import dark.library.PowerSystems;

public class TileEntityRunnableMachine extends TileEntityUniversalRunnable
{
	public static String powerToggleItemID = "battery";
	
	protected boolean runPowerless = false;

	@Override
	public void updateEntity()
	{
		/* CREATES FREE POWER IF NO POWER PROVIDER IS FOUND OR BLOCK WAS SET TO RUN WITHOUT POWER */
		if (this.wattsReceived < this.getWattBuffer() && (this.runPowerless || PowerSystems.runPowerLess(PowerSystems.INDUSTRIALCRAFT, PowerSystems.BUILDCRAFT, PowerSystems.MEKANISM)))
		{
			this.wattsReceived += Math.max(this.getWattBuffer() - this.wattsReceived, 0);
		}
		
		super.updateEntity();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.wattsReceived = nbt.getDouble("wattsReceived");
		this.runPowerless = nbt.getBoolean("shouldPower");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("wattsReceived", this.wattsReceived);
		nbt.setBoolean("shouldPower", this.runPowerless);
	}

	/**
	 * Sets this machine to run without power only if the given stack match an ore directory name
	 */
	public void toggleInfPower(ItemStack item)
	{
		for (ItemStack stack : OreDictionary.getOres(this.powerToggleItemID))
		{
			if (stack.isItemEqual(item))
			{
				this.runPowerless = !this.runPowerless;
				break;
			}
		}
	}
}
