package dark.assembly.machine.frame;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.IRotatable;
import dark.api.parts.INetworkPart;
import dark.api.parts.ITileNetwork;

public class TileEntityFrame extends TileEntity implements INetworkPart, IRotatable
{
    /** Do we have blocks connected to the side */
    private boolean[] hasConnectionSide = new boolean[6];
    List<TileEntity> tileConnections = new ArrayList<TileEntity>();
    /** Direction that we are facing though it and its opposite are the same */
    private ForgeDirection getFace = ForgeDirection.DOWN;

    private NetworkFrameRail network;

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
            if(((INetworkPart) ent).getTileNetwork() != this.getTileNetwork())
            {
                this.getTileNetwork().mergeNetwork(((INetworkPart) ent).getTileNetwork(), this);
            }
        }
        if (ent2 instanceof TileEntityFrame && (this.getFace == ((TileEntityFrame) ent2).getDirection() || this.getFace.getOpposite() == ((TileEntityFrame) ent2).getDirection()))
        {
            this.tileConnections.add(ent2);
            if(((INetworkPart) ent2).getTileNetwork() != this.getTileNetwork())
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

}
