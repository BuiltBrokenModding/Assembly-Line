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
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ColorCode;
import dark.api.ColorCode.IColorCoded;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.helpers.Pair;
import dark.fluid.common.BlockFM;

public class BlockPipe extends BlockFM
{

    public static int waterFlowRate = 3000;

    public BlockPipe()
    {
        super(BlockPipe.class, "FluidPipe", Material.iron);
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
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof TileEntityPipe)
        {
            meta = ((TileEntityPipe) tile).pipeData.ordinal();
        }

        return new ItemStack(blockID, 1, meta & 32);
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (PipeData data : PipeData.values())
        {
            if (data.restrictedCode != null)
            {
                data.enabled = FluidHelper.hasRestrictedStack(data.restrictedCode.ordinal());
            }
            if (data.enabled)
            {
                data.itemStack = new ItemStack(par1, 1, data.ordinal());
                par3List.add(data.itemStack);

            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6)
    {
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        super.breakBlock(world, x, y, z, par5, par6);

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
        if (world.getBlockMetadata(x, y, z) < 16)
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
                TileEntity tile = par1World.getBlockTileEntity(par2, par3, par4);
                if (tile instanceof TileEntityPipe)
                {
                    this.dropBlockAsItem_do(par1World, par2, par3, par4, ((TileEntityPipe) tile).pipeData.itemStack);
                    return;
                }
                this.dropBlockAsItem_do(par1World, par2, par3, par4, new ItemStack(this.blockID, 1, meta));
            }
        }
    }

    public static enum PipeData
    {
        BLACK_PIPE(ColorCode.BLACK),
        RED_PIPE(ColorCode.RED),
        GREEN_PIPE(ColorCode.GREEN),
        BROWN_PIPE(ColorCode.BROWN),
        BLUE_PIPE(ColorCode.BLUE),
        PURPLE_PIPE(ColorCode.PURPLE),
        CYAN_PIPE(ColorCode.CYAN),
        SILVER_PIPE(ColorCode.SILVER),
        GREY_PIPE(ColorCode.GREY),
        PINK_PIPE(ColorCode.PINK),
        LIME_PIPE(ColorCode.LIME),
        YELLOW_PIPE(ColorCode.YELLOW),
        LIGHTBLUE_PIPE(ColorCode.LIGHTBLUE),
        MAGENTA_PIPE(ColorCode.MAGENTA),
        ORANGE_PIPE(ColorCode.ORANGE),
        WHITE_PIPE(ColorCode.WHITE),
        OIL_PIPE(true, ColorCode.BLACK),
        FUEL_PIPE(true, ColorCode.YELLOW),
        LAVA_PIPE(true, ColorCode.RED),
        WATER_PIPE(true, ColorCode.BLUE),
        WASTE_PIPE(true, ColorCode.BROWN),
        PIPE6(false),
        PIPE7(false),
        PIPE8(false),
        PIPE9(false),
        PIPE10(false),
        PIPE11(false),
        PIPE12(false),
        PIPE13(false),
        PIPE14(false),
        IRON_PIPE();

        public boolean enabled = true;
        public ColorCode colorCode, restrictedCode;
        public ItemStack itemStack;

        private PipeData(ColorCode color)
        {
            this.colorCode = color;
        }

        private PipeData()
        {
            this(ColorCode.UNKOWN);
        }

        private PipeData(Boolean enabled)
        {
            this(ColorCode.UNKOWN);
            this.enabled = true;
        }

        private PipeData(Boolean enabled, ColorCode restrictedCode)
        {
            this(enabled);
            this.restrictedCode = restrictedCode;
        }

        public static PipeData get(Object obj)
        {
            if (obj instanceof Integer && ((Integer) obj) < PipeData.values().length)
            {
                return PipeData.values()[((Integer) obj)];
            }
            return IRON_PIPE;
        }
    }
}
