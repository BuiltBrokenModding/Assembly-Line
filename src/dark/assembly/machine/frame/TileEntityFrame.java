package dark.assembly.machine.frame;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.IRotatable;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.tilenetwork.INetworkPart;
import dark.api.tilenetwork.ITileNetwork;
import dark.core.interfaces.IBlockActivated;
import dark.core.network.ISimplePacketReceiver;
import dark.core.network.PacketHandler;
import dark.machines.DarkMain;

/** A non-updating tileEntity that represents the logic behind the frame. It contains rotation and
 * connection information. As well provides a way for a tile network to be created to provide a
 * uniform existence between all frame blocks in the rail
 * 
 * @author DarkGuardsman */
public class TileEntityFrame extends TileEntity implements INetworkPart, IRotatable, ISimplePacketReceiver, IBlockActivated
{
    /** Do we have blocks connected to the side */
    private boolean[] hasConnectionSide = new boolean[6];
    List<TileEntity> tileConnections = new ArrayList<TileEntity>();
    /** Direction that we are facing though it and its opposite are the same */
    private ForgeDirection getFace = ForgeDirection.DOWN;

    private NetworkFrameRail network;

    @SideOnly(Side.CLIENT)
    private float renderRotation = 0;

    /** Adds a connection side. Connections are items that link a block to the rail. This way the
     * rail knows to move the block when it moves */
    public void addConnectorSide(ForgeDirection side)
    {
        this.hasConnectionSide[side.ordinal()] = true;
    }

    /** Removes a connection side */
    public void removeConnectionSide(ForgeDirection side)
    {
        this.hasConnectionSide[side.ordinal()] = true;
    }

    /** Do we have a connection on the side */
    public boolean hasConnectionSide(ForgeDirection side)
    {
        return this.hasConnectionSide[side.ordinal()];
    }

    /** Called from the block when the player right clicks with a connector item */
    public boolean attachConnection(ForgeDirection side)
    {
        if (!hasConnectionSide(side))
        {
            this.addConnectorSide(side);
            return this.hasConnectionSide(side);
        }
        return false;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        if (entityPlayer != null && entityPlayer.getHeldItem() != null)
        {
            //TODO check for connector item then call attachConnection
            //If connection call returns true remove item from player inv
        }
        return false;
    }

    /* ***********************************************
     * Tile Network Code
     ************************************************/
    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        return type == Connection.NETWORK && (dir == getFace || dir == getFace.getOpposite());
    }

    @Override
    public List<TileEntity> getNetworkConnections()
    {
        return tileConnections;
    }

    @Override
    public void refresh()
    {
        this.tileConnections.clear();
        TileEntity ent = new Vector3(this).modifyPositionFromSide(this.getFace).getTileEntity(this.worldObj);
        TileEntity ent2 = new Vector3(this).modifyPositionFromSide(this.getFace.getOpposite()).getTileEntity(this.worldObj);

        if (ent instanceof TileEntityFrame && (this.getFace == ((TileEntityFrame) ent).getDirection() || this.getFace.getOpposite() == ((TileEntityFrame) ent).getDirection()))
        {
            this.tileConnections.add(ent);
            if (((INetworkPart) ent).getTileNetwork() != this.getTileNetwork())
            {
                this.getTileNetwork().mergeNetwork(((INetworkPart) ent).getTileNetwork(), this);
            }
        }
        if (ent2 instanceof TileEntityFrame && (this.getFace == ((TileEntityFrame) ent2).getDirection() || this.getFace.getOpposite() == ((TileEntityFrame) ent2).getDirection()))
        {
            this.tileConnections.add(ent2);
            if (((INetworkPart) ent2).getTileNetwork() != this.getTileNetwork())
            {
                this.getTileNetwork().mergeNetwork(((INetworkPart) ent2).getTileNetwork(), this);
            }
        }
    }

    @Override
    public NetworkFrameRail getTileNetwork()
    {
        if (!(this.network instanceof NetworkFrameRail))
        {
            this.network = new NetworkFrameRail(this);
        }
        return this.network;
    }

    @Override
    public void setTileNetwork(ITileNetwork network)
    {
        if (this.network instanceof NetworkFrameRail)
        {
            this.network = (NetworkFrameRail) network;
        }
    }

    /* ***********************************************
     * Rotation code
     ************************************************/

    @Override
    public ForgeDirection getDirection()
    {
        return this.getFace;
    }

    @Override
    public void setDirection(ForgeDirection direection)
    {
        this.getFace = direection;
    }

    /* ***********************************************
     * Load/Save/Packet code
     ************************************************/
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getTilePacket(DarkMain.CHANNEL, this, "Desc", this.getTileNetwork().rotation, (byte) this.getFace.ordinal());
    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput data, Player player)
    {
        if (this.worldObj.isRemote)
        {
            if (id.equalsIgnoreCase("Desc"))
            {
                this.renderRotation = data.readFloat();
                this.getFace = ForgeDirection.getOrientation(data.readByte());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

}
