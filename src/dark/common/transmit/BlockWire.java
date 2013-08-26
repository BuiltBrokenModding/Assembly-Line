package dark.common.transmit;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import dark.core.DarkMain;
import dark.core.blocks.BlockMachine;

public class BlockWire extends BlockMachine
{

    public BlockWire(Configuration config, int blockID)
    {
        super("DMWire", config, blockID, Material.cloth);
        this.setStepSound(soundClothFootstep);
        this.setResistance(0.2F);
        this.setHardness(0.1f);
        this.setBlockBounds(0.3f, 0.3f, 0.3f, 0.7f, 0.7f, 0.7f);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        Block.setBurnProperties(this.blockID, 30, 60);
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX +"CopperWire");
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityWire();
    }
}
