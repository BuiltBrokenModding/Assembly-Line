package dark.fluid.common.machines;

import com.dark.IndustryTabs;
import com.dark.prefab.BlockMachine;

import net.minecraft.block.material.Material;
import dark.fluid.common.FluidMech;

public abstract class BlockFM extends BlockMachine
{

    public BlockFM(String name, Material material)
    {
        super(FluidMech.CONFIGURATION, name, material);
        this.setCreativeTab(IndustryTabs.tabHydraulic());
    }
}
