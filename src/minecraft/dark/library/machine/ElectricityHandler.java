package dark.library.machine;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.electricity.ElectricityPack;

/** An optional easy way for you to handle electrical storage without hassle.
 *
 * @author Calclavia */
public class ElectricityHandler
{
	public IElectrical tileEntity;
	public float energyStored = 0;

	public ElectricityHandler(IElectrical tileEntity)
	{
		this.tileEntity = tileEntity;
	}

	public ElectricityHandler(IElectrical tileEntity, float energyStored)
	{
		this(tileEntity);
		this.setEnergyStored(energyStored);
	}

	public float receiveElectricity(ElectricityPack receive, boolean doReceive)
	{
		if (receive != null)
		{
			float prevEnergyStored = this.getEnergyStored();
			float newStoredEnergy = this.getEnergyStored() + receive.getWatts();

			if (doReceive)
			{
				this.setEnergyStored(newStoredEnergy);
			}

			return Math.max(newStoredEnergy - prevEnergyStored, 0);
		}

		return 0;
	}

	public float receiveElectricity(float energy, boolean doReceive)
	{
		return this.receiveElectricity(ElectricityPack.getFromWatts(energy, this.tileEntity.getVoltage()), doReceive);
	}

	public ElectricityPack provideElectricity(ElectricityPack request, boolean doProvide)
	{
		if (request != null)
		{
			float requestedEnergy = Math.min(request.getWatts(), this.energyStored);

			if (doProvide)
			{
				this.setEnergyStored(this.energyStored - requestedEnergy);
			}

			return ElectricityPack.getFromWatts(requestedEnergy, this.tileEntity.getVoltage());
		}

		return new ElectricityPack();
	}

	public ElectricityPack provideElectricity(float energy, boolean doProvide)
	{
		return this.provideElectricity(ElectricityPack.getFromWatts(energy, this.tileEntity.getVoltage()), doProvide);
	}

	public ElectricityHandler setEnergyStored(float energy)
	{
		this.energyStored = energy;
		return this;
	}

	public float getEnergyStored()
	{
		return this.energyStored;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		this.energyStored = nbt.getFloat("energyStored");
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setFloat("energyStored", this.energyStored);
	}
}
