package dark.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidBlock;

public interface IGasBlock extends IFluidBlock
{
    /** Can a fracking machine harvest this gas underground */
    public boolean canFrackerHarvest(TileEntity entity);
}
