package dark.common.transmit;

import net.minecraftforge.common.ForgeDirection;
import dark.common.DarkMain;
import dark.prefab.TileEntityMachine;

public class TileEntityLaserEmitter extends TileEntityMachine
{

    /** Facing direction of the tile and not the laser */
    public ForgeDirection getFacingDirection()
    {
        int meta = 0;
        if (this.worldObj != null)
        {
            meta = (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 6);
        }
        return ForgeDirection.getOrientation(meta);
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return direction == this.getFacingDirection().getOpposite();
    }

    @Override
    public float getRequest(ForgeDirection side)
    {
        return 0;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getChannel()
    {
        return DarkMain.CHANNEL;
    }

}
