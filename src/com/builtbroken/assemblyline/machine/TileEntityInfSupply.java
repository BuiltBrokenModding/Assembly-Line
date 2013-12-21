package com.builtbroken.assemblyline.machine;

import net.minecraftforge.common.ForgeDirection;

import com.builtbroken.minecraft.prefab.TileEntityEnergyMachine;

public class TileEntityInfSupply extends TileEntityEnergyMachine
{

    @Override
    public long onReceiveEnergy(ForgeDirection from, long receive, boolean doReceive)
    {
        return 0;
    }

    @Override
    public long onExtractEnergy(ForgeDirection from, long request, boolean doExtract)
    {
        return request;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return true;
    }

    @Override
    public long getMaxEnergyStored()
    {
        return Long.MAX_VALUE;
    }
}
