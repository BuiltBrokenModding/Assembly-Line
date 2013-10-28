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

import com.builtbroken.common.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ColorCode;
import dark.api.ColorCode.IColorCoded;
import dark.core.prefab.helpers.FluidHelper;
import dark.fluid.common.BlockFM;
import dark.fluid.common.FMRecipeLoader;

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
        return PipeMaterial.getDropItem(world, x, y, z);
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (PipeMaterial data : PipeMaterial.values())
        {
            par3List.add(data.getStack());
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

    /** Enum to hold info about each pipe material. Values are by default and some can change with
     * pipe upgrades.
     *
     * @Note unsupportedFluids should only be used by filters. All pipes should allow all fluid
     * types. However, pipes that can't support the fluid should have an effect. Eg no gas support
     * should cause the pipe to leak. No molten support should cause the pipe to take damage.
     *
     * @author DarkGuardsman */
    public static enum PipeMaterial
    {
        /** Simple water only pipe. Should render open toped when it can */
        WOOD("wood", false, true, false, 50, 200),
        /** Gas only pipe */
        GLASS("wood", true, false, false, 100, 300),
        /** Another version of the wooden pipe */
        STONE("wood", false, true, false, 200, 1000),
        /** Cheap fluid pipe */
        TIN("wood", false, true, false, 300, 1000),
        /** Cheap fluid pipe */
        COPPER("wood", false, true, false, 400, 1000),
        /** First duel gas and fluid pipe */
        IRON("wood", true, true, false, 500, 1000),
        /** Fluid movement pipe that doesn't work well with pressure */
        GOLD("wood", true, true, false, 200, 2000),
        /** Cheap molten metal pipe */
        OBBY("wood", false, true, true, 1000, 1000),
        /** Very strong fluid and gas support pipe. Should also support molten metal as long as they
         * don't stay in the pipe too long. */
        STEEL("wood", true, true, false, 10000, 3000),
        /** Weaker equal to steel pipes. Should also support steam very well */
        BRONZE("wood", true, true, false, 6000, 2000),
        /** Hell fluids only. Meaning lava, and molten metals. Water should turn to steam, fuel and
         * oil should cause an explosion around the pipe */
        HELL("wood", true, true, true, 10000, 5000, "water", "fuel", "oil");
        public String matName = "material";
        List<String> unsupportedFluids = new ArrayList<String>();
        public boolean supportsAllFluids = false;
        public boolean supportsAllGas = false;
        public boolean canSupportGas = false;
        public boolean canSupportFluids = false;
        public boolean canSupportMoltenFluids = false;
        public int maxPressure = 1000;
        public int maxVolume = 2000;
        /** Materials are stored as meta were there sub types are stored by NBT. Item versions of the
         * pipes are still meta so there is a set spacing to allow for a large but defined range of
         * sub pipes */
        public static int spacing = 1000;

        private PipeMaterial()
        {
            supportsAllFluids = true;
            supportsAllGas = true;
            canSupportMoltenFluids = true;
        }

        private PipeMaterial(String name, boolean gas, boolean fluid, boolean molten, String... strings)
        {
            this.matName = name;
            this.canSupportGas = gas;
            this.canSupportFluids = fluid;
            this.canSupportMoltenFluids = molten;
        }

        private PipeMaterial(String name, boolean gas, boolean fluid, boolean molten, int pressure, int volume, String... strings)
        {
            this(name, gas, fluid, molten, strings);
            this.maxPressure = pressure;
            this.maxVolume = volume;
        }

        public ItemStack getStack()
        {
            return getStack(1);
        }

        public ItemStack getStack(ColorCode color)
        {
            return getStack(1, color);
        }

        public ItemStack getStack(int s)
        {
            return new ItemStack(FMRecipeLoader.blockPipe, s, (this.ordinal() * spacing));
        }

        public ItemStack getStack(int s, ColorCode color)
        {
            return new ItemStack(FMRecipeLoader.blockPipe, s, (this.ordinal() * spacing) + color.ordinal());
        }

        public static ItemStack getDropItem(World world, int x, int y, int z)
        {
            int meta = world.getBlockMetadata(x, y, z);
            TileEntity ent = world.getBlockTileEntity(x, y, z);
            if (ent instanceof IColorCoded)
            {
                meta += ((IColorCoded) ent).getColor().ordinal();
            }
            return new ItemStack(FMRecipeLoader.blockPipe, 1, meta);
        }
    }

    public static enum PipeSubType
    {
        //TODO list sub types then create an enum interface to have each sub handle its own metadata
        COLOR();
    }
}
