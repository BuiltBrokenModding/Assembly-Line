package dark.fluid.common.prefab;

import java.util.Random;

import com.dark.tilenetwork.ITileConnector;
import com.dark.tilenetwork.prefab.NetworkTileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import dark.api.IToolReadOut;

public abstract class TileEntityFluidDevice extends TileEntityAdvanced implements IToolReadOut, ITileConnector
{
    public Random random = new Random();

    @Override
    public void invalidate()
    {
        super.invalidate();
        NetworkTileEntities.invalidate(this);
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
    {
        if (tool != null && tool == EnumTools.PIPE_GUAGE)
        {
            return " IndirectlyPower:" + this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        }
        return null;
    }
}
