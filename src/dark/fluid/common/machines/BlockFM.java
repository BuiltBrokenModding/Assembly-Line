package dark.fluid.common.machines;

import net.minecraft.block.material.Material;

import com.dark.IndustryTabs;
import com.dark.prefab.BlockMachine;

import dark.assembly.AssemblyLine;

public abstract class BlockFM extends BlockMachine
{

    public BlockFM(String name, Material material)
    {
        super(AssemblyLine.CONFIGURATION, name, material);
        this.setCreativeTab(IndustryTabs.tabHydraulic());
    }
}
