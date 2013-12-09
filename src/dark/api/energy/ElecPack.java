package dark.api.energy;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.electricity.ElectricityPack;

/** Wrapper for storing information about electricity packets
 * 
 * @author DarkGuardsman */
public class ElecPack extends EnergyPack
{
    protected float volts, amps;

    public ElecPack(Object... data)
    {
        super(EnergyPack.ELECTRIC_ENERGY, 0, data);
    }

    public ElecPack(float amps, float volts, Object... data)
    {
        super("Electric", amps * volts, data);
        this.volts = volts;
        this.amps = amps;
    }

    public ElecPack setVoltAmp(float volt, float amp)
    {
        this.volts = volt;
        this.amps = amp;
        this.energyStored = volts * amps;
        return this;
    }

    public ElecPack setVoltWatt(float volt, float watt)
    {
        this.volts = volt;
        this.amps = watt / volt;
        this.energyStored = watt;
        return this;
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setFloat("volts", this.volts);
        nbt.setFloat("amps", this.amps);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        this.volts = nbt.getFloat("volts");
        this.amps = nbt.getFloat("amps");
    }

    public float getVolt()
    {
        return volts;
    }

    public float solveVolt()
    {
        return energyStored / amps;
    }

    public float getAmps()
    {
        return amps;
    }

    public float solveAmps()
    {
        return energyStored / volts;
    }

    public float getWatts()
    {
        return this.energyStored;
    }

    public float solveWatts()
    {
        return amps * volts;
    }

    @Override
    public ElecPack clone()
    {
        return new ElecPack(0, 0, this.getData()).setVoltAmp(this.volts, this.amps);
    }

    @Override
    public String toString()
    {
        return "ElecPack [Amps:" + this.amps + " Volts:" + this.volts + "]";
    }

    public boolean isEqual(ElecPack electricityPack)
    {
        return this.amps == electricityPack.amps && this.volts == electricityPack.volts;
    }

    public boolean isEqual(ElectricityPack electricityPack)
    {
        return this.amps == electricityPack.amperes && this.volts == electricityPack.voltage;
    }

}
