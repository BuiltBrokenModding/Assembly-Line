package dark.api.energy;


/** Container for energy data
 * 
 * @author DarkGuardsman */
public class EnergyPacket
{
    protected String energyType;
    protected float energyStored;
    protected Object[] data;

    public EnergyPacket(String type, float energySum, Object... data)
    {
        this.energyType = type;
        this.energyStored = energySum;
        this.data = data;
    }
}
