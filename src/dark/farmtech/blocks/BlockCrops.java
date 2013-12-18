package dark.farmtech.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import com.dark.DarkCore;

import dark.farmtech.FarmTech;

public class BlockCrops extends Block implements IPlantable
{

    public BlockCrops()
    {
        super(FarmTech.CONFIGURATION.getBlock("Crop", DarkCore.getNextID()).getInt(), Material.vine);
        this.setUnlocalizedName("FarmCrops");
    }

    @Override
    public EnumPlantType getPlantType(World world, int x, int y, int z)
    {
        return EnumPlantType.Plains;
    }

    @Override
    public int getPlantID(World world, int x, int y, int z)
    {
        return this.blockID;
    }

    @Override
    public int getPlantMetadata(World world, int x, int y, int z)
    {
        return -1;
    }

}
