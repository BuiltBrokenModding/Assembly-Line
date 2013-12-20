package dark.machines.machines;

import com.dark.prefab.TileEntityEnergyMachine;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;

public abstract class TileEntityGenerator extends TileEntityEnergyMachine
{
    /** Run time left */
    protected int burnTime = 0;

    public TileEntityGenerator()
    {
        super();
    }

    public TileEntityGenerator(float wattsPerTick)
    {
        super(wattsPerTick);
    }

    public TileEntityGenerator(float wattsPerTick, float maxEnergy)
    {
        super(wattsPerTick, maxEnergy);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!this.worldObj.isRemote && this.enabled)
        {
            if (this.burnTime <= 0)
            {
                this.consumeFuel();
            }
            if (this.isFunctioning())
            {
                this.burnTime--;
                this.produceAllSides();
            }
        }
    }

    @Override
    public boolean canFunction()
    {
        return this.enabled && !this.isDisabled() && this.hasFuel();
    }

    public boolean hasFuel()
    {
        return burnTime > 0;
    }

    /** Called when the burn time is bellow 10 and the machine needs to keep running */
    public abstract void consumeFuel();

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return this.JOULES_PER_TICK;
    }

    /* ********************************************
     * Electricity reception logic -  all of which is set to zero to prevent input from wires
     ***********************************************/

    @Override
    public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
    {
        return 0;
    }

    @Override
    public float receiveElectricity(ElectricityPack receive, boolean doReceive)
    {
        return 0;
    }

    @Override
    public float receiveElectricity(float energy, boolean doReceive)
    {
        return 0;
    }

    @Override
    public boolean consumePower(float watts, boolean doDrain)
    {
        return true;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }
}
