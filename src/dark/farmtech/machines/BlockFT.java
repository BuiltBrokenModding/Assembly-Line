package dark.farmtech.machines;

import net.minecraft.block.material.Material;
import dark.core.prefab.machine.BlockMachine;
import dark.core.registration.ModObjectRegistry.BlockBuildData;
import dark.farmtech.FarmTech;

/** Prefab class for all farm blocks to remove the need for some configuration of the super class
 *
 * @author Darkguardsman */
public abstract class BlockFT extends BlockMachine
{

    public BlockFT(Class<? extends BlockMachine> blockClass, String name, Material material)
    {
        super(new BlockBuildData(blockClass, name, material).setCreativeTab(FarmTech.TabFarmTech));
    }

}
