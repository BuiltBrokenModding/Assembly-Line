package dark.fluid.common.pipes;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.fluid.INetworkPipe;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.helpers.Pair;
import dark.fluid.common.BlockFM;
import dark.fluid.common.FMRecipeLoader;
import dark.fluid.common.FluidMech;

public class BlockPipe extends BlockFM
{
    public BlockPipe(int id, String name)
    {
        super(name, id, Material.iron);
        this.setBlockBounds(0.30F, 0.30F, 0.30F, 0.70F, 0.70F, 0.70F);
        this.setHardness(1f);
        this.setResistance(3f);

    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int damageDropped(int par1)
    {
        return par1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);

        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof INetworkPipe)
        {
            ((INetworkPipe) tileEntity).refresh();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof INetworkPipe)
        {
            ((INetworkPipe) tileEntity).refresh();
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        if (this.blockID == FMRecipeLoader.blockGenPipe.blockID)
        {
            return new TileEntityGenericPipe();
        }
        return new TileEntityPipe();
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        int blockID = world.getBlockId(x, y, z);
        return new ItemStack(blockID, 1, meta);
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < 16; i++)
        {
            if (this.blockID == FMRecipeLoader.blockGenPipe.blockID || FluidHelper.hasRestrictedStack(i))
            {
                par3List.add(new ItemStack(par1, 1, i));
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6)
    {
        super.breakBlock(world, x, y, z, par5, par6);
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity instanceof TileEntityPipe)
        {
            IFluidTank tank = ((TileEntityPipe) entity).getTank(0);
            if (tank != null && tank.getFluid() != null && tank.getFluid().getFluid() != null && tank.getFluid().amount > 0)
            {
                if (tank.getFluid().getFluid().getName().equalsIgnoreCase("water"))
                {
                    world.setBlock(x, y, z, Block.waterStill.blockID);
                }
                if (tank.getFluid().getFluid().getName().equalsIgnoreCase("lava"))
                {
                    world.setBlock(x, y, z, Block.lavaStill.blockID);
                }
            }
        }
    }

    @Override
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
    {
        if (this.blockID == FMRecipeLoader.blockGenPipe.blockID)
        {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta != colour)
            {
                world.setBlockMetadataWithNotify(x, y, z, colour, 3);
                this.onNeighborBlockChange(world, x, y, z, world.getBlockId(x, y, z));
                return true;
            }
        }
        return false;
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        list.add(new Pair<String, Class<? extends TileEntity>>("FluidPipe", TileEntityPipe.class));
        list.add(new Pair<String, Class<? extends TileEntity>>("ColoredPipe", TileEntityGenericPipe.class));
    }
}
