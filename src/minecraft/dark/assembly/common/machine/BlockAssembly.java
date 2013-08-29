package dark.assembly.common.machine;

import net.minecraft.block.material.Material;
import net.minecraft.util.Icon;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.TabAssemblyLine;
import dark.prefab.BlockMachine;

public abstract class BlockAssembly extends BlockMachine
{
    public Icon machine_icon;

    public BlockAssembly(int id, Material material, String name)
    {
        super(name, AssemblyLine.CONFIGURATION, id, material);
        this.setUnlocalizedName(name);
        this.setCreativeTab(TabAssemblyLine.INSTANCE);
    }

}