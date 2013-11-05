package dark.farmtech.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import dark.core.prefab.ModPrefab;
import dark.farmtech.FarmTech;

public class BlockCrops extends Block
{

    public BlockCrops()
    {
        super(FarmTech.CONFIGURATION.getBlock("Crop", ModPrefab.getNextID()).getInt(), Material.vine);
        this.setUnlocalizedName("FarmCrops");
    }

}
