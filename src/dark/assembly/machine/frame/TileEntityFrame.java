package dark.assembly.machine.frame;

import java.util.List;

import dark.api.parts.INetworkPart;
import dark.api.parts.ITileNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityFrame extends TileEntity implements INetworkPart
{

    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<TileEntity> getNetworkConnections()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void refresh()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public ITileNetwork getTileNetwork()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTileNetwork(ITileNetwork network)
    {
        // TODO Auto-generated method stub

    }

}
