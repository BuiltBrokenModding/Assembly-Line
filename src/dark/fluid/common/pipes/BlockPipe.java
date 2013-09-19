package dark.fluid.common.pipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ColorCode;
import dark.api.ColorCode.IColorCoded;
import dark.api.fluid.INetworkPipe;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.helpers.Pair;
import dark.fluid.common.BlockFM;
import dark.fluid.common.FMRecipeLoader;

public class BlockPipe extends BlockFM
{

    public static int waterFlowRate = 3000;

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
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
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
        for (int i = 0; i < 32; i++)
        {
            if (this.blockID != FMRecipeLoader.blockGenPipe.blockID)
            {
                if (i >= 16 || i < 16 && FluidHelper.hasRestrictedStack(i))
                {
                    par3List.add(new ItemStack(par1, 1, i));
                }
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
            if (world.getBlockTileEntity(x, y, z) instanceof IColorCoded)
            {
                ((IColorCoded) world.getBlockTileEntity(x, y, z)).setColor(ColorCode.get(colour));
            }
        }
        return false;
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        list.add(new Pair<String, Class<? extends TileEntity>>("FluidPipe", TileEntityPipe.class));
        list.add(new Pair<String, Class<? extends TileEntity>>("ColoredPipe", TileEntityPipe.class));
    }

    @Override
    public boolean hasExtraConfigs()
    {
        return true;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        BlockPipe.waterFlowRate = config.get("settings", "FlowRate", BlockPipe.waterFlowRate, "Base value for flow rate is based off of water. It is in milibuckets so 1000 equals one bucket of fluid").getInt();

    }

    @Override
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        if (!par1World.isRemote)
        {
            if (par1World.rand.nextFloat() <= par6)
            {
                int meta = 0;
                if (par1World.getBlockTileEntity(par2, par3, par4) instanceof IColorCoded)
                {
                    meta = ((IColorCoded) par1World.getBlockTileEntity(par2, par3, par4)).getColor().ordinal() & 15;
                }
                this.dropBlockAsItem_do(par1World, par2, par3, par4, new ItemStack(this.blockID, 1, meta));
            }
        }
    }
}
