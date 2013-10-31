package dark.fluid.common.machines;

import net.minecraft.block.material.Material;
import dark.core.common.DMCreativeTab;
import dark.core.prefab.machine.BlockMachine;
import dark.core.registration.ModObjectRegistry.BlockBuildData;
import dark.fluid.common.FluidMech;

public abstract class BlockFM extends BlockMachine
{

    public BlockFM(Class<? extends BlockFM> blockClass, String name, Material material)
    {
        super(new BlockBuildData(blockClass, name, material).setConfigProvider(FluidMech.CONFIGURATION).setCreativeTab(DMCreativeTab.tabHydrualic));
    }
}
