package com.builtbroken.assemblyline.generators;

import java.util.EnumSet;

import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import net.minecraftforge.common.ForgeDirection;

import com.builtbroken.assemblyline.machine.TileEntityGenerator;

public class TileEntitySolarPanel extends TileEntityGenerator
{
    public TileEntitySolarPanel()
    {
        super(0, 1);
    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.of(ForgeDirection.DOWN);
    }

    @Override
    public void consumeFuel()
    {
        this.burnTime = BlockSolarPanel.tickRate;
        if (!this.worldObj.isRemote && this.ticks % BlockSolarPanel.tickRate == 0)
        {

            if (this.worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord) && !this.worldObj.provider.hasNoSky)
            {
                if (this.worldObj.isDaytime())
                {
                    this.setJoulesPerTick(BlockSolarPanel.wattDay);
                    if (this.worldObj.isThundering() || this.worldObj.isRaining())
                    {
                        this.setJoulesPerTick(BlockSolarPanel.wattStorm);
                    }
                }
                else
                {
                    this.setJoulesPerTick(BlockSolarPanel.wattNight);
                    if (this.worldObj.isThundering() || this.worldObj.isRaining())
                    {
                        this.setJoulesPerTick(0);
                    }
                }
                this.setJoulesPerTick(this.JOULES_PER_TICK + this.JOULES_PER_TICK * (this.worldObj.provider instanceof ISolarLevel ? (int) ((ISolarLevel) this.worldObj.provider).getSolarEnergyMultiplier() : 0));
            }
            else
            {
                this.setJoulesPerTick(0);
            }
        }

    }
}
