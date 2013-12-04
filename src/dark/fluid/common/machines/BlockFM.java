package dark.fluid.common.machines;

import net.minecraft.block.material.Material;
import dark.core.ModObjectRegistry.BlockBuildData;
import dark.core.prefab.machine.BlockMachine;
import dark.fluid.common.FluidMech;
import dark.machines.common.DMCreativeTab;

public abstract class BlockFM extends BlockMachine
{

    public BlockFM(Class<? extends BlockFM> blockClass, String name, Material material)
    {
        super(new BlockBuildData(blockClass, name, material).setConfigProvider(FluidMech.CONFIGURATION).setCreativeTab(DMCreativeTab.tabHydrualic));
    }
}
