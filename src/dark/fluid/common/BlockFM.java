package dark.fluid.common;

import net.minecraft.block.material.Material;
import dark.core.blocks.BlockMachine;

public abstract class BlockFM extends BlockMachine
{

    public BlockFM(String name, int blockID, Material material)
    {
        super(name, FluidMech.CONFIGURATION, blockID, material);
        this.setCreativeTab(FluidMech.TabFluidMech);
    }

}
