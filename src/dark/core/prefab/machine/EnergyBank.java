package dark.core.prefab.machine;

import universalelectricity.core.block.IElectricalStorage;
import universalelectricity.core.electricity.ElectricityPack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

/** Basic energy bank class designed to remove most of the energy buffer code from a machine */
public class EnergyBank implements IElectricalStorage
{
    protected float capacity;
    protected float energyStored;
    protected TileEntity tile;

    public EnergyBank(int capacity)
    {
        this.capacity = capacity;
    }

    public EnergyBank(int amount, int capacity)
    {
        this(capacity);
        this.energyStored = amount;
    }

    /** A non-side specific version of receiveElectricity for you to optionally use it internally. */
    public float receiveElectricity(ElectricityPack receive, boolean doReceive)
    {

        if (receive != null)
        {
            float prevEnergyStored = this.getEnergyStored();
            float newStoredEnergy = Math.min(this.getEnergyStored() + receive.getWatts(), this.getMaxEnergyStored());

            if (doReceive)
            {
                this.setEnergyStored(newStoredEnergy);
            }

            return Math.max(newStoredEnergy - prevEnergyStored, 0);
        }

        return 0;
    }

    public float receiveElectricity(float energy, float voltage, boolean doReceive)
    {
        return this.receiveElectricity(ElectricityPack.getFromWatts(energy, voltage), doReceive);
    }

    /** A non-side specific version of provideElectricity for you to optionally use it internally. */
    public ElectricityPack provideElectricity(ElectricityPack request, boolean doProvide)
    {
        if (request != null)
        {
            float requestedEnergy = Math.min(request.getWatts(), this.energyStored);

            if (doProvide)
            {
                this.setEnergyStored(this.energyStored - requestedEnergy);
            }

            return ElectricityPack.getFromWatts(requestedEnergy, request.voltage);
        }

        return new ElectricityPack();
    }

    public ElectricityPack provideElectricity(float energy, float voltage,  boolean doProvide)
    {
        return this.provideElectricity(ElectricityPack.getFromWatts(energy, voltage), doProvide);
    }

    public EnergyBank readFromNBT(NBTTagCompound nbt)
    {
        this.energyStored = nbt.getFloat("energyStored");
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("energyStored", this.energyStored);
        return nbt;
    }

    @Override
    public void setEnergyStored(float energy)
    {
        this.energyStored = energy;
    }

    @Override
    public float getEnergyStored()
    {
        return this.energyStored;
    }

    @Override
    public float getMaxEnergyStored()
    {
        return this.capacity;
    }
}
