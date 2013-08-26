package dark.common.transmit;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import dark.core.blocks.BlockMachine;

public class BlockWire extends BlockMachine
{

    public BlockWire(Configuration config, int blockID)
    {
        super("DMWire", config, blockID, Material.cloth);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setBlockBounds(0, 0, 0, 1, .3f, 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityWire();
    }
}
