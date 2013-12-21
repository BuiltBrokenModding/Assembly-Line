package com.builtbroken.assemblyline.machine;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.electricity.ElectricityPack;

import com.builtbroken.minecraft.prefab.TileEntityEnergyMachine;

public abstract class TileEntityGenerator extends TileEntityEnergyMachine
{
    /** Run time left */
    protected int burnTime = 0;

    public TileEntityGenerator()
    {
        super();
    }

    public TileEntityGenerator(long wattsPerTick)
    {
        super(wattsPerTick);
    }

    public TileEntityGenerator(long wattsPerTick, long maxEnergy)
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

    /* ********************************************
     * Electricity reception logic -  all of which is set to zero to prevent input from wires
     ***********************************************/

    @Override
    public long onReceiveEnergy(ForgeDirection from, long receive, boolean doReceive)
    {
        return 0;
    }
}
