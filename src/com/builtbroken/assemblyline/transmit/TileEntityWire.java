package com.builtbroken.assemblyline.transmit;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.CompatibilityModule;
import universalelectricity.api.energy.EnergyNetworkLoader;
import universalelectricity.api.energy.IConductor;
import universalelectricity.api.energy.IEnergyNetwork;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorHelper;
import universalelectricity.core.net.EnergyNetwork;

import com.builtbroken.minecraft.helpers.ColorCode;
import com.builtbroken.minecraft.prefab.TileEntityAdvanced;

public class TileEntityWire extends TileEntityAdvanced implements IConductor
{
    protected int updateTick = 1;
    protected ColorCode color = ColorCode.BLACK;

    private IEnergyNetwork network;

    public TileEntity[] connections = new TileEntity[6];

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            if (ticks % this.updateTick == 0)
            {
                this.updateTick = this.worldObj.rand.nextInt(5) * 40 + 20;
                this.refresh();
            }
        }
    }

    public void refresh()
    {
        this.connections = new TileEntity[6];
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            if (this.canConnect(direction.getOpposite()))
            {
                TileEntity entity = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this), direction);
                if (CompatibilityModule.isHandler(entity))
                {
                    this.connections[direction.ordinal()] = entity;
                }
            }
        }
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public TileEntity[] getConnections()
    {
        if (this.connections == null)
        {
            this.refresh();
        }
        return this.connections;
    }

    @Override
    public IEnergyNetwork getNetwork()
    {
        if (!(this.network instanceof IEnergyNetwork))
        {
            this.network = EnergyNetworkLoader.getNewNetwork(this);
        }
        return this.network;
    }

    @Override
    public void setNetwork(IEnergyNetwork network)
    {
        if (network instanceof IEnergyNetwork)
        {
            this.network = (EnergyNetwork) network;
        }
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return true;
    }

    @Override
    public long onReceiveEnergy(ForgeDirection from, long receive, boolean doReceive)
    {
        if (this.canConnect(from) && network != null)
        {
            return network.produce(receive);
        }
        return 0;
    }

    @Override
    public long onExtractEnergy(ForgeDirection from, long extract, boolean doExtract)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getEnergyLoss()
    {
        return 0;
    }

    @Override
    public long getEnergyCapacitance()
    {
        return BlockWire.energyMax;
    }

}
