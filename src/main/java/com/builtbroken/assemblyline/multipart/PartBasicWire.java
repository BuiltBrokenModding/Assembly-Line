package com.builtbroken.assemblyline.multipart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.CompatibilityModule;
import universalelectricity.api.energy.EnergyNetworkLoader;
import universalelectricity.api.energy.IConductor;
import universalelectricity.api.energy.IEnergyNetwork;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorHelper;


//@UniversalClass
public class PartBasicWire extends PartAdvanced implements IConductor
{
    private IEnergyNetwork network;

    protected Object[] connections = new Object[6];

    /** Universal Electricity conductor functions. */
    @Override
    public long onReceiveEnergy(ForgeDirection from, long receive, boolean doReceive)
    {
        return this.getNetwork().produce(this, from.getOpposite(), receive, doReceive);
    }

    @Override
    public long onExtractEnergy(ForgeDirection from, long request, boolean doExtract)
    {
        return 0;
    }

    @Override
    public IEnergyNetwork getNetwork()
    {
        if (this.network == null)
        {
            this.setNetwork(EnergyNetworkLoader.getNewNetwork(this));
        }

        return this.network;
    }

    @Override
    public void setNetwork(IEnergyNetwork network)
    {
        this.network = network;
    }

    @Override
    public void preRemove()
    {
        if (!world().isRemote)
        {
            this.getNetwork().split(this);
        }

        super.preRemove();
    }

    @Override
    public boolean doesTick()
    {
        return false;
    }

    @Override
    public Object[] getConnections()
    {
        return this.connections;
    }

    /** EXTERNAL USE Can this wire be connected by another block? */
    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        Vector3 connectPos = new Vector3(tile()).modifyPositionFromSide(direction);
        TileEntity connectTile = connectPos.getTileEntity(world());

        if (connectTile instanceof IConductor)
        {
            return false;
        }

        return CompatibilityModule.isHandler(connectTile);
    }

    public boolean canConnectTo(Object obj)
    {
        if (obj instanceof PartBasicWire)
        {
            return true;
        }
        else if (!(obj instanceof IConductor))
        {
            return CompatibilityModule.isHandler(obj);
        }

        return false;
    }

    /** Recalculates all the network connections */
    protected void recalculateConnections()
    {
        this.connections = new Object[6];
        /** Calculate all external connections with this conductor. */
        for (byte i = 0; i < 6; i++)
        {
            ForgeDirection side = ForgeDirection.getOrientation(i);

            if (this.canConnect(side))
            {
                TileEntity tileEntity = VectorHelper.getTileEntityFromSide(world(), new Vector3(tile()), side);
                connections[i] = tileEntity;
            }
        }
    }

    @Override
    public float getResistance()
    {
        return 0.0000000168f;
    }

    @Override
    public long getCurrentCapacity()
    {
        return 100000;
    }

    @Override
    public String getType()
    {
        return "assembly_line_basic_wire";
    }

    /** NBT Data */
    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        if (nbt.hasKey("saveBuffer"))
            this.getNetwork().setBufferFor(this, nbt.getLong("saveBuffer"));
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void save(NBTTagCompound nbt)
    {
        super.save(nbt);
        if (this.getNetwork().getBufferOf(this) > 0)
            nbt.setLong("saveBuffer", this.getNetwork().getBufferOf(this));
    }

}