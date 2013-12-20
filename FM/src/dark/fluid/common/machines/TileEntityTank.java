package dark.fluid.common.machines;

import com.dark.tilenetwork.INetworkPart;
import com.dark.tilenetwork.ITileNetwork;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import dark.api.fluid.INetworkFluidPart;
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

    @Override
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
