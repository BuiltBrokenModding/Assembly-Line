package dark.library.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import dark.core.api.PowerSystems;

public class TileEntityRunnableMachine extends TileEntityElectricMachine
{
	public TileEntityRunnableMachine(float d)
	{
		super(d * 2, d);
	}

	/** Forge Ore Directory name of the item to toggle power */
	public static String powerToggleItemID = "battery";

	/** Power Systems this machine can support */
	private PowerSystems[] powerList = new PowerSystems[] { PowerSystems.BUILDCRAFT, PowerSystems.MEKANISM };

	/** Does this tile have power to run and do work */
	public boolean canRun()
	{
		boolean power = this.getEnergyStored() >= this.tickEnergy || this.runWithOutPower || PowerSystems.runPowerLess(powerList);
		return !this.isDisabled() && power;
	}

	/** Called when a player activates the tile's block */
	public boolean onPlayerActivated(EntityPlayer player)
	{
		if (player != null && player.capabilities.isCreativeMode)
		{
			ItemStack itemStack = player.getHeldItem();
			if (itemStack != null)
			{
				for (ItemStack stack : OreDictionary.getOres(TileEntityRunnableMachine.powerToggleItemID))
				{
					if (stack.isItemEqual(itemStack))
					{
						this.runWithOutPower = !this.runWithOutPower;
						//player.sendChatToPlayer(chatmessagecomponent)
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public float getRequest(ForgeDirection direction)
	{
		return Math.max(this.getMaxEnergyStored() - this.getEnergyStored(), 0);
	}

	@Override
	public int powerRequest(ForgeDirection from)
	{
		if (this.canConnect(from) && !this.runWithOutPower)
		{
			return (int) Math.ceil(this.getRequest(from) * PowerSystems.TO_BC_RATIO);
		}

		return 0;
	}

	@Override
	public float getProvide(ForgeDirection direction)
	{
		return 0;
	}
}
