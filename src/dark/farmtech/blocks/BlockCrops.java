package dark.farmtech.blocks;

import dark.farmtech.FarmTech;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockCrops extends Block
{

    public BlockCrops()
    {
        super(FarmTech.CONFIGURATION.getBlock("Crop", FarmTech.getNextID()).getInt(), Material.vine);
        this.setUnlocalizedName("FarmCrops");
    }

}
