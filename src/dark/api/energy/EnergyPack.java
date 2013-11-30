package dark.api.energy;

import net.minecraft.nbt.NBTTagCompound;
import dark.api.DataPack;

/** Container for energy data
 *
 * @author DarkGuardsman */
public class EnergyPack extends DataPack
{
    protected String energyType;
    protected float energyStored;

    public EnergyPack(String type, float energySum, Object... data)
    {
        super(data);
        this.energyType = type;
        this.energyStored = energySum;
    }

    public float getJoules()
    {
        return energyStored;
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setFloat("energy", this.energyStored);
        nbt.setString("energyType", this.energyType);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        this.energyStored = nbt.getFloat("energy");
        this.energyType = nbt.getString("energyType");
    }

    @Override
    public EnergyPack clone()
    {
        return new EnergyPack(this.energyType, this.energyStored, this.getData());
    }

    public boolean isEqual(EnergyPack pack)
    {
        return this.energyType.equalsIgnoreCase(pack.energyType);
    }

    @Override
    public String toString()
    {
        return "EnergyPack [Joules:" + this.energyStored + "]";
    }
}
