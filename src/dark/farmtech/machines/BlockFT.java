package dark.farmtech.machines;

import net.minecraft.block.material.Material;
import dark.core.DMCreativeTab;
import dark.core.ModObjectRegistry.BlockBuildData;
import dark.core.prefab.machine.BlockMachine;

/** Prefab class for all farm blocks to remove the need for some configuration of the super class
 * 
 * @author Darkguardsman */
public abstract class BlockFT extends BlockMachine
{

    public BlockFT(Class<? extends BlockMachine> blockClass, String name, Material material)
    {
        super(new BlockBuildData(blockClass, name, material).setCreativeTab(DMCreativeTab.tabIndustrial));
    }

}
