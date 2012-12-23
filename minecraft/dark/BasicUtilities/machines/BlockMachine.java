package dark.BasicUtilities.machines;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dark.BasicUtilities.ItemRenderHelper;
import dark.BasicUtilities.tanks.TileEntityLTank;

public class BlockMachine extends BlockContainer
{

    public BlockMachine(int id)
    {
        super(id, Material.iron);
        this.setBlockName("Machine");
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setRequiresSelfNotify();
        this.blockIndexInTexture = 26;
        this.setHardness(1f);
        this.setResistance(5f);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return ItemRenderHelper.renderID;
    }

    public int damageDropped(int meta)
    {
        if (meta < 4) { return 0; }
        return meta;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta)
    {
        // TODO Auto-generated method stub
        if (meta < 4) { return new TileEntityPump(); }
        if (meta == 4)
        {
            // return new TileEntityCondenser();
        }
        if (meta == 5) { return new TileEntityLTank(); }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
