package com.builtbroken.assemblyline.machine.belt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

import com.builtbroken.assemblyline.api.IBelt;
import com.builtbroken.assemblyline.machine.TileEntityAssembly;
import com.dark.prefab.TileEntityEnergyMachine;
import com.dark.tilenetwork.IMotionPath;
import com.dark.tilenetwork.INetworkPart;
import com.dark.tilenetwork.ITileNetwork;
import com.dark.tilenetwork.prefab.NetworkItemSupply;

public class TileEntityConveyor extends TileEntityEnergyMachine implements IMotionPath, IBelt, INetworkPart
{
    public enum SlantType
    {
        NONE,
        UP,
        DOWN,
        TOP
    }

    /** Entities that are ignored allowing for other tiles to interact with them */
    public List<Entity> ignoreList = new ArrayList<Entity>();

    private SlantType slantType = SlantType.NONE;

    private NetworkItemSupply network;

    /** Tiles that are connected to this */
    public List<TileEntity> connectedTiles = new ArrayList<TileEntity>();

    public TileEntityConveyor()
    {
        super(0.001f);
    }

    @Override
    public boolean canFunction()
    {
        return super.canFunction() && !this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean canMoveEntity(Entity entity)
    {
        if (entity != null)
        {
            if (entity.posY > (this.yCoord + 0.3) && entity.posX > this.xCoord && entity.posZ > this.zCoord)
            {
                if (entity.posY < (this.yCoord + 0.8) && entity.posX < this.xCoord && entity.posZ < this.zCoord)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Vector3 getMotion(Entity entity)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDirection(ForgeDirection facingDirection)
    {
        this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal(), 3);
    }

    @Override
    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(this.getBlockMetadata());
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return direction == ForgeDirection.DOWN;
    }

    /** NBT Data */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.slantType = SlantType.values()[nbt.getByte("slant")];
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("slant", (byte) this.slantType.ordinal());
    }

    @Override
    public List<Entity> getAffectedEntities()
    {
        return worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 0.8, this.zCoord + 1));
    }

    @Override
    public void ignoreEntity(Entity entity)
    {
        if (network != null)
        {
            network.ignoreEntity(entity);
        }

    }

    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        return type == Connection.NETWORK;
    }

    @Override
    public List<TileEntity> getNetworkConnections()
    {
        return this.connectedTiles;
    }

    @Override
    public void refresh()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            this.connectedTiles.clear();
            Vector3 face = new Vector3(this).modifyPositionFromSide(this.getDirection());
            Vector3 back = new Vector3(this).modifyPositionFromSide(this.getDirection().getOpposite());
            TileEntity front, rear;
            if (this.slantType == SlantType.DOWN)
            {
                face.translate(new Vector3(0, -1, 0));
                back.translate(new Vector3(0, 1, 0));
            }
            else if (this.slantType == SlantType.UP)
            {
                face.translate(new Vector3(0, 1, 0));
                back.translate(new Vector3(0, -1, 0));
            }
            front = face.getTileEntity(this.worldObj);
            rear = back.getTileEntity(this.worldObj);
            if (front instanceof IMotionPath && front instanceof INetworkPart)
            {
                this.getTileNetwork().mergeNetwork(((TileEntityAssembly) front).getTileNetwork(), this);
                this.connectedTiles.add(front);
            }
            if (rear instanceof IMotionPath && rear instanceof INetworkPart)
            {
                this.getTileNetwork().mergeNetwork(((TileEntityAssembly) rear).getTileNetwork(), this);
                this.connectedTiles.add(rear);
            }

        }

    }

    @Override
    public NetworkItemSupply getTileNetwork()
    {
        if (!(this.network instanceof NetworkItemSupply))
        {
            this.network = new NetworkItemSupply(this);
        }
        return this.network;
    }

    @Override
    public void setTileNetwork(ITileNetwork network)
    {
        if (network instanceof NetworkItemSupply)
        {
            this.network = (NetworkItemSupply) network;
        }
    }

}
