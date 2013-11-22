package dark.core.common.blocks;

import java.awt.Color;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.IGasBlock;
import dark.core.common.DMCreativeTab;
import dark.core.common.DarkMain;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.fluids.EnumGas;

/** Gas that is designed to generate underground in the same way as an ore
 * 
 * TODO code actual gas behavior such as expanding to fill an area but at the same time losing
 * volume
 * 
 * @author DarkGuardsman */
public class BlockGasOre extends Block implements IGasBlock
{
    public static final int[] volumePerMeta = new int[] { 10, 35, 75, 125, 250, 500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 12800, 256000, 512000 };

    public BlockGasOre()
    {
        super(DarkMain.CONFIGURATION.getBlock("GasBlock", ModPrefab.getNextID()).getInt(), Material.air);
        this.setUnlocalizedName("DMBlockGas");
        this.setCreativeTab(DMCreativeTab.tabIndustrial);
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        if (!world.isRemote)
        {
            if (rand.nextFloat() > 0.5f)
            {

                final Vector3 vec = new Vector3(x, y, z);
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
                {
                    int meta = world.getBlockMetadata(x, y, z);

                    Vector3 sVec = vec.clone().modifyPositionFromSide(dir);
                    int sMeta = sVec.getBlockMetadata(world);
                    int blockID = sVec.getBlockID(world);
                    Block block = Block.blocksList[blockID];

                    if (block != null && block.isAirBlock(world, x, y, z) && blockID != this.blockID)
                    {
                        if (meta == 0)
                        {
                            world.setBlockToAir(x, y, z);
                            break;
                        }
                        else
                        {
                            world.setBlock(x, y, z, this.blockID, meta / 2, 2);
                            sVec.setBlock(world, this.blockID, meta / 2, 2);
                            break;
                        }
                    }
                    else if (blockID == this.blockID && meta > sMeta)
                    {
                        meta += sMeta;
                        world.setBlock(x, y, z, this.blockID, meta / 2, 2);
                        sVec.setBlock(world, this.blockID, meta / 2, 2);
                        break;
                    }
                }
            }
        }
    }

    /* IFluidBlock */
    @Override
    public FluidStack drain(World world, int x, int y, int z, boolean doDrain)
    {
        int meta = world.getBlockMetadata(x, y, z);
        FluidStack fluid = new FluidStack(EnumGas.NATURAL_GAS.getGas(), volumePerMeta[meta]);
        if (doDrain || fluid == null)
        {
            world.setBlockToAir(x, y, z);
        }
        return fluid;
    }

    @Override
    public boolean canDrain(World world, int x, int y, int z)
    {
        return true;
    }

    @Override
    public boolean canFrackerHarvest(TileEntity entity)
    {
        return true;
    }

    @Override
    public Fluid getFluid()
    {
        return EnumGas.NATURAL_GAS.getGas();
    }

    @Override
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + "gas");
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return Color.GREEN.getRGB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int par1)
    {
        return this.getBlockColor();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int l = 0;
        int i1 = 0;
        int j1 = 0;

        for (int k1 = -1; k1 <= 1; ++k1)
        {
            for (int l1 = -1; l1 <= 1; ++l1)
            {
                int i2 = par1IBlockAccess.getBiomeGenForCoords(par2 + l1, par4 + k1).getBiomeGrassColor();
                l += (i2 & 16711680) >> 16;
                i1 += (i2 & 65280) >> 8;
                j1 += i2 & 255;
            }
        }

        return (l / 9 & 255) << 16 | (i1 / 9 & 255) << 8 | j1 / 9 & 255;
    }

}
