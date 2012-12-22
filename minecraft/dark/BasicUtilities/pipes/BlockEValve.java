package dark.BasicUtilities.pipes;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import dark.BasicUtilities.BasicUtilitiesMain;

public class BlockEValve extends BlockContainer
{

    public BlockEValve(int par1)
    {
        super(par1, Material.iron);
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityEValve();
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return true;
    }

    public int getRenderType()
    {
        return 0;
    }

    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }

    @Override
    public void onNeighborBlockChange(World par1World, int x, int y, int z, int side)
    {
        super.onNeighborBlockChange(par1World, x, y, z, side);

        TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IRedstoneReceptor)
        {
            if (par1World.isBlockIndirectlyGettingPowered(x, y, z))
            {
                ((IRedstoneReceptor) par1World.getBlockTileEntity(x, y, z)).onPowerOn();
            }
        }
    }

    public void breakBlock(World world, int x, int y, int z, int par5, int par6)
    {
        super.breakBlock(world, x, y, z, par5, par6);
        TileEntity ent = world.getBlockTileEntity(x, y, z);
        Random furnaceRand = new Random();
        if (ent instanceof TileEntityPipe)
        {
            TileEntityEValve pipe = (TileEntityEValve) ent;
            int meta = pipe.type.ordinal();
            float var8 = furnaceRand.nextFloat() * 0.8F + 0.1F;
            float var9 = furnaceRand.nextFloat() * 0.8F + 0.1F;
            float var10 = furnaceRand.nextFloat() * 0.8F + 0.1F;
            EntityItem var12 = new EntityItem(world, (double) ((float) x + var8), (double) ((float) y + var9),
                    (double) ((float) z + var10), new ItemStack(BasicUtilitiesMain.itemEValve, 1, meta));
            float var13 = 0.05F;
            var12.motionX = (double) ((float) furnaceRand.nextGaussian() * var13);
            var12.motionY = (double) ((float) furnaceRand.nextGaussian() * var13 + 0.2F);
            var12.motionZ = (double) ((float) furnaceRand.nextGaussian() * var13);
            world.spawnEntityInWorld(var12);
        }
    }
}
