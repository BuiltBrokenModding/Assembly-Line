package com.builtbroken.assemblyline.transmit;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntitySwitchWire extends TileEntityWire
{
    protected boolean activated = true;

    @Override
    public TileEntity[] getAdjacentConnections()
    {
        if (activated)
        {
            return super.getAdjacentConnections();
        }
        return null;
    }

    public void setActivate(boolean yes)
    {
        boolean p = this.activated;
        this.activated = yes;
        if (p != this.activated)
        {
            this.refresh();
        }
    }

    public boolean getActivated()
    {
        return this.activated;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return this.getActivated();
    }
}
