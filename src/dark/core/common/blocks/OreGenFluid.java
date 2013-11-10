package dark.core.common.blocks;

import java.util.Random;

import dark.core.common.CoreRecipeLoader;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraftforge.fluids.FluidStack;
import universalelectricity.prefab.ore.OreGenBase;

/** Used to generate very small pockets of fluid underground
 * 
 * @author DarkGuardsman */
public class OreGenFluid extends OreGenBase
{

    public int minGenerateLevel;
    public int maxGenerateLevel;
    public int amountPerChunk;
    public int amountPerBranch;
    public int replaceID;

    public FluidStack stack;

    /** Dimensions to ignore ore generation */
    public boolean ignoreSurface = false;
    public boolean ignoreNether = true;
    public boolean ignoreEnd = true;

    public OreGenFluid(String name, String oreDiectionaryName, FluidStack stack, int replaceID, int minGenerateLevel, int maxGenerateLevel, int amountPerChunk, int amountPerBranch)
    {
        super(name, oreDiectionaryName, new ItemStack(CoreRecipeLoader.blockGas, 1), "", 0);
        this.stack = stack;
        this.minGenerateLevel = minGenerateLevel;
        this.maxGenerateLevel = maxGenerateLevel;
        this.amountPerChunk = amountPerChunk;
        this.amountPerBranch = amountPerBranch;
        this.replaceID = replaceID;
    }

    @Override
    public void generate(World world, Random random, int varX, int varZ)
    {
        try
        {
            for (int i = 0; i < this.amountPerChunk; i++)
            {
                int x = varX + random.nextInt(16);
                int z = varZ + random.nextInt(16);
                int y = random.nextInt(Math.max(this.maxGenerateLevel - this.minGenerateLevel, 0)) + this.minGenerateLevel;
                this.generateReplace(world, random, x, y, z);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error generating ore: " + this.name);
            e.printStackTrace();
        }
    }

    public boolean generateReplace(World world, Random par2Random, int par3, int par4, int par5)
    {
        float var6 = par2Random.nextFloat() * (float) Math.PI;
        double var7 = par3 + 8 + MathHelper.sin(var6) * this.amountPerBranch / 8.0F;
        double var9 = par3 + 8 - MathHelper.sin(var6) * this.amountPerBranch / 8.0F;
        double var11 = par5 + 8 + MathHelper.cos(var6) * this.amountPerBranch / 8.0F;
        double var13 = par5 + 8 - MathHelper.cos(var6) * this.amountPerBranch / 8.0F;
        double var15 = par4 + par2Random.nextInt(3) - 2;
        double var17 = par4 + par2Random.nextInt(3) - 2;

        for (int var19 = 0; var19 <= this.amountPerBranch; ++var19)
        {
            double var20 = var7 + (var9 - var7) * var19 / this.amountPerBranch;
            double var22 = var15 + (var17 - var15) * var19 / this.amountPerBranch;
            double var24 = var11 + (var13 - var11) * var19 / this.amountPerBranch;
            double var26 = par2Random.nextDouble() * this.amountPerBranch / 16.0D;
            double var28 = (MathHelper.sin(var19 * (float) Math.PI / this.amountPerBranch) + 1.0F) * var26 + 1.0D;
            double var30 = (MathHelper.sin(var19 * (float) Math.PI / this.amountPerBranch) + 1.0F) * var26 + 1.0D;
            int var32 = MathHelper.floor_double(var20 - var28 / 2.0D);
            int var33 = MathHelper.floor_double(var22 - var30 / 2.0D);
            int var34 = MathHelper.floor_double(var24 - var28 / 2.0D);
            int var35 = MathHelper.floor_double(var20 + var28 / 2.0D);
            int var36 = MathHelper.floor_double(var22 + var30 / 2.0D);
            int var37 = MathHelper.floor_double(var24 + var28 / 2.0D);

            for (int x = var32; x <= var35; ++x)
            {
                double var39 = (x + 0.5D - var20) / (var28 / 2.0D);

                if (var39 * var39 < 1.0D)
                {
                    for (int y = var33; y <= var36; ++y)
                    {
                        double var42 = (y + 0.5D - var22) / (var30 / 2.0D);

                        if (var39 * var39 + var42 * var42 < 1.0D)
                        {
                            for (int z = var34; z <= var37; ++z)
                            {
                                double var45 = (z + 0.5D - var24) / (var28 / 2.0D);

                                int blockid = world.getBlockId(x, y, z);
                                Block block = Block.blocksList[blockid];
                                if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D && (block == null || block.isAirBlock(world, x, y, z) || block.canBeReplacedByLeaves(world, x, y, z) || blockid == this.replaceID))
                                {
                                    BlockGasOre.placeAndCreate(world, x, y, z, stack);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean isOreGeneratedInWorld(World world, IChunkProvider chunkGenerator)
    {
        if (!this.shouldGenerate)
        {
            return false;
        }
        if (this.ignoreSurface && chunkGenerator instanceof ChunkProviderGenerate)
        {
            return false;
        }
        if (this.ignoreNether && chunkGenerator instanceof ChunkProviderHell)
        {
            return false;
        }
        if (this.ignoreEnd && chunkGenerator instanceof ChunkProviderEnd)
        {
            return false;
        }
        return true;
    }
}
