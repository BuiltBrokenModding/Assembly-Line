package dark.assembly.common.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import dark.api.parts.INetworkEnergyPart;
import dark.assembly.common.AssemblyLine;
import dark.core.prefab.machine.TileEntityEnergyMachine;
import dark.core.prefab.tilenetwork.NetworkSharedPower;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

/** A class to be inherited by all machines on the assembly line. This class acts as a single peace
 * in a network of similar tiles allowing all to share power from one or more sources
 *
 * @author DarkGuardsman */
public abstract class TileEntityAssembly extends TileEntityEnergyMachine implements INetworkEnergyPart
{
    /** lowest value the network can update at */
    public static int refresh_min_rate = 20;
    /** range by which the network can update at */
    public static int refresh_diff = 9;
    /** Network used to link assembly machines together */
    private NetworkAssembly assemblyNetwork;
    /** Tiles that are connected to this */
    public List<TileEntity> connectedTiles = new ArrayList<TileEntity>();
    /** Random instance */
    public Random random = new Random();
    /** Random rate by which this tile updates its network connections */
    private int updateTick = 1;

    public TileEntityAssembly(float wattsPerTick)
    {
        super(wattsPerTick);
    }

    public TileEntityAssembly(float wattsPerTick, float maxEnergy)
    {
        super(wattsPerTick, maxEnergy);
    }

    @Override
    public void invalidate()
    {
        NetworkTileEntities.invalidate(this);
        if (this.getTileNetwork() != null)
        {
            this.getTileNetwork().splitNetwork(this.worldObj, this);
        }
        super.invalidate();
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!this.worldObj.isRemote)
        {

            if (ticks % updateTick == 0)
            {
                this.updateTick = (random.nextInt(1 + refresh_diff) + refresh_min_rate);
                this.refresh();
            }
        }

        this.onUpdate();
    }

    @Override
    public boolean canFunction()
    {
        //TODO add check for network power
        return super.canFunction() || AssemblyLine.REQUIRE_NO_POWER;
    }

    /** Same as updateEntity */
    @Deprecated
    public void onUpdate()
    {

    }

    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        return true;
    }

    @Override
    public void refresh()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            this.connectedTiles.clear();

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                TileEntity tileEntity = new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj);
                if (tileEntity instanceof TileEntityAssembly && ((TileEntityAssembly) tileEntity).canTileConnect(Connection.NETWORK, dir.getOpposite()))
                {
                    this.getTileNetwork().merge(((TileEntityAssembly) tileEntity).getTileNetwork(), this);
                    connectedTiles.add(tileEntity);
                }
            }
        }
    }

    @Override
    public List<TileEntity> getNetworkConnections()
    {
        return this.connectedTiles;
    }

    @Override
    public NetworkAssembly getTileNetwork()
    {
        if (this.assemblyNetwork == null)
        {
            this.assemblyNetwork = new NetworkAssembly(this);
        }
        return this.assemblyNetwork;
    }

    @Override
    public void setTileNetwork(NetworkTileEntities network)
    {
        if (network instanceof NetworkAssembly)
        {
            this.assemblyNetwork = (NetworkAssembly) network;
        }
    }

    @Override
    public boolean consumePower(float watts, boolean doDrain)
    {
        return ((NetworkSharedPower) this.getTileNetwork()).drainPower(this, watts, doDrain);
    }

    @Override
    public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
    {
        return this.getTileNetwork().dumpPower(this, receive.getWatts(), doReceive);
    }

    /** Amount of energy this tile runs on per tick */
    public double getWattLoad()
    {
        return .1;//100w
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return this.getTileNetwork().getNetworkDemand();
    }

    @Override
    public void togglePowerMode()
    {
        ((NetworkSharedPower) this.getTileNetwork()).setPowerLess(this.runPowerLess());
    }

    @Override
    public float getEnergyStored()
    {
        return ((NetworkSharedPower) this.getTileNetwork()).getEnergyStored();
    }

    @Override
    public float getPartEnergy()
    {
        return this.energyStored;
    }

    @Override
    public float getPartMaxEnergy()
    {
        return this.MAX_WATTS;
    }

    @Override
    public void setPartEnergy(float energy)
    {
        this.energyStored = energy;
    }

    @Override
    public boolean mergeDamage(String effect)
    {
        this.onDisable(20);
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public String toString()
    {
        return "[AssemblyTile]@" + (new Vector3(this).toString());
    }
}
