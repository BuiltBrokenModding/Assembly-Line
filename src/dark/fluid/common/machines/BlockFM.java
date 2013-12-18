package dark.fluid.common.machines;

import net.minecraft.block.material.Material;
import dark.core.DMCreativeTab;
import dark.core.prefab.machine.BlockMachine;
import dark.fluid.common.FluidMech;

public abstract class BlockFM extends BlockMachine
{

    public BlockFM(String name, Material material)
    {
        super(FluidMech.CONFIGURATION, name, material);
        this.setCreativeTab(DMCreativeTab.tabHydraulic());
    }
}
