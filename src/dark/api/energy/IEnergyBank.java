package dark.api.energy;

/** Applied to objects the store energy
 *
 * @author DarkGaurdsman */
public interface IEnergyBank
{
    /** Charges the unit with energy and returns the amount accepted */
    public float giveEnergy(EnergyPack pack, boolean doAdd);

    /** Can this unit accept energy of the type */
    public boolean canGiveEnergy(EnergyPack pack);

    /** Changes the unit with energy and returns the amount of energy discharged */
    public float takeEnergy(EnergyPack pack, boolean doRemove);

    /** Can this unit give away energy of the type */
    public boolean canTakeEnergy(EnergyPack pack);

    /** Sets the energy stored in this unit */
    public void setEnergy(EnergyPack pack);

    /** @return Energy Stored in this unit */
    public float getEnergy(String type);

    /** Sets the max energy this unit can store. */
    public void setMaxEnergy(EnergyPack pack);

    /** @return Upper limit of energy storage. */
    public float getMaxEnergy(String type);
}
