package dark.fluid.common.machines;

import com.dark.ModObjectRegistry.BlockBuildData;

import net.minecraft.block.material.Material;
import dark.core.DMCreativeTab;
import dark.core.prefab.machine.BlockMachine;
import dark.fluid.common.FluidMech;

public abstract class BlockFM extends BlockMachine
{

    public BlockFM(Class<? extends BlockFM> blockClass, String name, Material material)
    {
        super(new BlockBuildData(blockClass, name, material).setConfigProvider(FluidMech.CONFIGURATION).setCreativeTab(DMCreativeTab.tabHydraulic()));
    }
}
