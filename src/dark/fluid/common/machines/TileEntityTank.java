package dark.fluid.common.machines;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.api.fluid.INetworkFluidPart;
import dark.api.tilenetwork.INetworkPart;
import dark.api.tilenetwork.ITileNetwork;
import dark.api.tilenetwork.ITileConnector.Connection;
import dark.core.prefab.tilenetwork.fluid.NetworkFluidContainers;
import dark.fluid.common.prefab.TileEntityFluidNetworkTile;

public class TileEntityTank extends TileEntityFluidNetworkTile
{
    public TileEntityTank()
    {
        super(BlockTank.tankVolume);
    }

    @Override
    public NetworkFluidContainers getTileNetwork()
    {
        if (!(this.network instanceof NetworkFluidContainers))
        {
            this.setTileNetwork(new NetworkFluidContainers(this));
        }
        return (NetworkFluidContainers) this.network;
    }

    @Override
    public void setTileNetwork(ITileNetwork network)
    {
        if (network instanceof NetworkFluidContainers)
        {
            this.network = (NetworkFluidContainers) network;
        }
    }

    public void validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
    {
        if (!this.worldObj.isRemote)
        {
            if (tileEntity instanceof TileEntityTank)
            {
                if (this.canTileConnect(Connection.NETWORK, side.getOpposite()))
                {
                    this.getTileNetwork().mergeNetwork(((INetworkFluidPart) tileEntity).getTileNetwork(), (INetworkPart) tileEntity);
                    this.renderConnection[side.ordinal()] = true;
                    connectedBlocks.add(tileEntity);
                }
            }
        }
    }
}
