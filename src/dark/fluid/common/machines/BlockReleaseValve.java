package dark.fluid.common.machines;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import dark.fluid.common.BlockFM;
import dark.fluid.common.FluidMech;

public class BlockReleaseValve extends BlockFM
{
    public BlockReleaseValve(int par1)
    {
        super("ReleaseValve", par1, Material.iron);
        this.setHardness(1f);
        this.setResistance(5f);
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityReleaseValve();
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public int damageDropped(int meta)
    {
        return 0;
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

    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return new ItemStack(FluidMech.recipeLoader.blockReleaseValve, 1, 0);
    }
}
