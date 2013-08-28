package dark.fluid.common.machines;

import net.minecraft.tileentity.TileEntity;

public class TileEntityBoiler extends TileEntity
{

    public TileEntity[] connectedBlocks = new TileEntity[6];
    public int tankCount;

}
