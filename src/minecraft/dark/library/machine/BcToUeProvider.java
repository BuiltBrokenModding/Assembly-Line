package dark.library.machine;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import buildcraft.api.power.PowerProvider;
import dark.core.api.PowerSystems;

public class BcToUeProvider extends PowerProvider
{
	public TileEntityElectricMachine tileEntity;	

	public BcToUeProvider(TileEntityElectricMachine tile)
	{
		tileEntity = tile;
	}

	@Override
	public void receiveEnergy(float quantity, ForgeDirection from)
	{
		powerSources[from.ordinal()] = 2;

		tileEntity.receiveElectricity(from, new ElectricityPack((PowerSystems.BC3_RATIO * quantity), tileEntity.getVoltage()), true);

	}

	@Override
	public float useEnergy(float min, float max, boolean doUse)
	{
		float result = 0;

		if (tileEntity.getEnergyStored() >= min)
		{
			if (tileEntity.getEnergyStored() <= max)
			{
				result = (float) tileEntity.getEnergyStored();
				if (doUse)
				{
					tileEntity.setEnergyStored(0);
				}
			}
			else
			{
				result = max;
				if (doUse)
				{
					tileEntity.setEnergyStored(tileEntity.getEnergyStored() - max);
				}
			}
		}

		return result;

	}

	@Override
	public float getEnergyStored()
	{
		return (float) this.tileEntity.getEnergyStored();
	}

	@Override
	public int getMaxEnergyReceived()
	{
		return (int) Math.ceil(this.tileEntity.getMaxEnergyStored());
	}

	@Override
	public int getMaxEnergyStored()
	{
		return (int) Math.ceil(this.tileEntity.getMaxEnergyStored());
	}

}
