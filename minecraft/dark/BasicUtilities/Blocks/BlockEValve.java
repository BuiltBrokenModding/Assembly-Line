package dark.BasicUtilities.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.PipeTab;
import dark.BasicUtilities.Tile.TileEntityEValve;

public class BlockEValve extends BlockContainer
{

    public BlockEValve(int par1)
    {
        super(par1, Material.iron);
        this.setCreativeTab(PipeTab.INSTANCE);
        this.setHardness(1f);
        this.setResistance(5f);
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityEValve();
    }
    @Override
    public void onBlockAdded(World par1World, int x, int y, int z) 
    {
        this.checkForPower(par1World, x, y, z);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public String getTextureFile()
    {
        return BasicUtilitiesMain.BlOCK_PNG;
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int meta)
    {
        return meta;
    }

    public boolean renderAsNormalBlock()
    {
        return true;
    }

    public int getRenderType()
    {
        return 0;
    }

    public int damageDropped(int meta)
    {
        return meta;
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    @Override
    public void onNeighborBlockChange(World par1World, int x, int y, int z, int side)
    {
        super.onNeighborBlockChange(par1World, x, y, z, side);
        this.checkForPower(par1World, x, y, z);
        
    }
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        return new ItemStack(BasicUtilitiesMain.eValve,1,meta);        
    }
    public static void checkForPower(World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityEValve)
        {
            boolean powered = ((TileEntityEValve) tileEntity).isPowered;
            boolean beingPowered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockGettingPowered(x, y, z);
            if (powered && !beingPowered)
            {
                ((IRedstoneReceptor) world.getBlockTileEntity(x, y, z)).onPowerOff();
            }
            else if (!powered && beingPowered)
            {
                ((IRedstoneReceptor) world.getBlockTileEntity(x, y, z)).onPowerOn();
            }
        }
    }

}
