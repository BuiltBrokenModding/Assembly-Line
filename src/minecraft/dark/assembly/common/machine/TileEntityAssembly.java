package dark.assembly.common.machine;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.grid.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import dark.api.INetworkPart;
import dark.assembly.common.AssemblyLine;
import dark.core.blocks.TileEntityMachine;
import dark.core.tile.network.NetworkTileEntities;

/** A class to be inherited by all machines on the assembly line. This class acts as a single peace
 * in a network of similar tiles allowing all to share power from one or more sources
 * 
 * @author DarkGuardsman */
public abstract class TileEntityAssembly extends TileEntityMachine implements INetworkPart, IPacketReceiver, IConductor
{

    /** Is the tile currently powered allowing it to run */
    public boolean running = false;
    private boolean prevRun = false;
    /** Network used to link assembly machines together */
    private NetworkAssembly assemblyNetwork;
    /** Tiles that are connected to this */
    public List<TileEntity> connectedTiles = new ArrayList<TileEntity>();
    /** Random instance */
    public Random random = new Random();
    /** Random rate by which this tile updates its connections */
    private int updateTick = 1;

    public static enum AssemblyTilePacket
    {
        POWER(),
        NBT();
    }

    @Override
    public void invalidate()
    {
        NetworkAssembly.invalidate(this);
        if (this.getTileNetwork() != null)
        {
            this.getTileNetwork().splitNetwork(this.worldObj, this);
        }
        super.invalidate();
    }

    @Override
    public void updateEntity()
    {
        if (!this.worldObj.isRemote)
        {
            this.prevRun = this.running;
            super.updateEntity();
            if (ticks % updateTick == 0)
            {
                this.updateTick = ((int) random.nextInt(10) + 20);
                this.refresh();
            }
            this.running = this.canRun();
            if (running != prevRun)
            {
                Packet packet = PacketManager.getPacket(AssemblyLine.CHANNEL, this, AssemblyTilePacket.POWER.ordinal(), this.running);
                PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 64);
            }
        }

        this.onUpdate();
    }

    @Override
    public boolean canRun()
    {
        //TODO add check for network power
        return super.canRun() || AssemblyLine.REQUIRE_NO_POWER;
    }

    /** Same as updateEntity */
    public abstract void onUpdate();

    /** Checks to see if this assembly tile can run using several methods */
    public boolean isRunning()
    {
        return this.running;
    }

    /** Amount of energy this tile runs on per tick */
    public double getWattLoad()
    {
        return 1;
    }

    @Override
    public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
    {
        return entity != null && entity instanceof TileEntityAssembly;
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
                if (tileEntity instanceof TileEntityAssembly && ((TileEntityAssembly) tileEntity).canTileConnect(this, dir.getOpposite()))
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
    public NetworkTileEntities getTileNetwork()
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
    public boolean mergeDamage(String effect)
    {
        this.onDisable(20);
        return true;
    }

    public String toString()
    {
        return "[AssemblyTile]@" + (new Vector3(this).toString());
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
    {
        boolean packetSize = false;
        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
            DataInputStream dis = new DataInputStream(bis);

            int id = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();
            int z = dis.readInt();
            int pId = dis.readInt();

            this.simplePacket(pId, dis, player);

            /** DEBUG PACKET SIZE AND INFO */
            if (packetSize)
            {
                System.out.println("TileEntityAssembly>" + new Vector3(this) + ">>>Debug>>Packet" + pId + ">>Size>>bytes>>" + packet.data.length);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error Reading Packet for a TileEntityAssembly");
            e.printStackTrace();
        }

    }

    /** Handles reduced data from the main packet method
     * 
     * @param id - packet ID
     * @param dis - data
     * @param player - player
     * @return true if the packet was used */
    public boolean simplePacket(int id, DataInputStream dis, EntityPlayer player)
    {
        try
        {
            if (this.worldObj.isRemote)
            {
                if (id == AssemblyTilePacket.POWER.ordinal())
                {
                    this.running = dis.readBoolean();
                    return true;
                }
                if (id == AssemblyTilePacket.NBT.ordinal())
                {
                    this.readFromNBT(Packet.readNBTTagCompound(dis));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public IElectricityNetwork getNetwork()
    {
        return (this.getTileNetwork() instanceof IElectricityNetwork ? (IElectricityNetwork) this.getTileNetwork() : null);
    }

    @Override
    public void setNetwork(IElectricityNetwork network)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public TileEntity[] getAdjacentConnections()
    {
        return this.connectedTiles.toArray(new TileEntity[this.connectedTiles.size()]);
    }

    @Override
    public float getResistance()
    {
        return 0.01f;
    }

    @Override
    public float getCurrentCapacity()
    {
        return 1000;
    }

}
