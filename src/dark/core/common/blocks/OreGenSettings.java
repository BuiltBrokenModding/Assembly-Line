package dark.core.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;

public class OreGenSettings
{
    public int minGenerateLevel;
    public int maxGenerateLevel;
    public int amountPerChunk;
    public int amountPerBranch;
    public int replaceID;

    public boolean ignoreSurface = false;
    public boolean ignoreNether = true;
    public boolean ignoreEnd = true;

    public boolean shouldGenerate = false;

    public int havestLevel;
    public String harvestTool;

    public String oreName;
    public ItemStack oreStack;

    /** @param name - name of the ore to be used in the config file
     * @param stack - itemStack of the ore, amount doesn't matter
     * @param harvestTool - tool needed to harvest tool
     * @param harvestLevel - level of hardness need to harvest the ore */
    public OreGenSettings(String name, ItemStack stack, String harvestTool, int harvestLevel, int replaceID, int minGenerateLevel, int maxGenerateLevel, int amountPerChunk, int amountPerBranch)
    {
        if (stack != null)
        {
            this.harvestTool = harvestTool;
            this.oreStack = stack;
            this.havestLevel = harvestLevel;

            this.minGenerateLevel = minGenerateLevel;
            this.maxGenerateLevel = maxGenerateLevel;
            this.amountPerChunk = amountPerChunk;
            this.amountPerBranch = amountPerBranch;
            this.replaceID = replaceID;

            MinecraftForge.setBlockHarvestLevel(Block.blocksList[stack.itemID], stack.getItemDamage(), harvestTool, harvestLevel);
        }
        else
        {
            FMLLog.severe("ItemStack is null while registering ore generation!");
        }
    }

    public OreGenSettings genStone(String name, ItemStack stack, int minGenerateLevel, int maxGenerateLevel, int amountPerChunk, int amountPerBranch)
    {
        return new OreGenSettings(name, stack, "pickAxe", 1, Block.stone.blockID, minGenerateLevel, maxGenerateLevel, amountPerChunk, amountPerBranch);
    }

    /** Is the ore gen enabled */
    public OreGenSettings enable(Configuration config)
    {
        this.shouldGenerate = shouldGenerateOre(config, this.oreName);
        return this;
    }

    /** Checks the config file and see if Universal Electricity should generate this ore */
    private static boolean shouldGenerateOre(Configuration configuration, String oreName)
    {
        boolean shouldGenerate = configuration.get("Ore_Generation", "Generate_" + oreName, true).getBoolean(true);
        configuration.save();
        return shouldGenerate;
    }

    public void generate(World world, Random random, int chunkCoordX, int chunkCoordZ)
    {
        try
        {
            for (int i = 0; i < this.amountPerChunk; i++)
            {
                int x = chunkCoordX + random.nextInt(16);
                int z = chunkCoordZ + random.nextInt(16);
                int y = random.nextInt(Math.max(this.maxGenerateLevel - this.minGenerateLevel, 0)) + this.minGenerateLevel;
                this.generateReplace(world, random, x, y, z);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error generating ore: " + this.oreName);
            e.printStackTrace();
        }
    }

    public boolean generateReplace(World world, Random random, int genX, int genY, int genZ)
    {
        float var6 = random.nextFloat() * (float) Math.PI;
        double var7 = genX + 8 + MathHelper.sin(var6) * this.amountPerBranch / 8.0F;
        double var9 = genX + 8 - MathHelper.sin(var6) * this.amountPerBranch / 8.0F;
        double var11 = genZ + 8 + MathHelper.cos(var6) * this.amountPerBranch / 8.0F;
        double var13 = genZ + 8 - MathHelper.cos(var6) * this.amountPerBranch / 8.0F;
        double var15 = genY + random.nextInt(3) - 2;
        double var17 = genY + random.nextInt(3) - 2;

        for (int branchCount = 0; branchCount <= this.amountPerBranch; ++branchCount)
        {
            double var20 = var7 + (var9 - var7) * branchCount / this.amountPerBranch;
            double var22 = var15 + (var17 - var15) * branchCount / this.amountPerBranch;
            double var24 = var11 + (var13 - var11) * branchCount / this.amountPerBranch;
            double var26 = random.nextDouble() * this.amountPerBranch / 16.0D;
            double var28 = (MathHelper.sin(branchCount * (float) Math.PI / this.amountPerBranch) + 1.0F) * var26 + 1.0D;
            double var30 = (MathHelper.sin(branchCount * (float) Math.PI / this.amountPerBranch) + 1.0F) * var26 + 1.0D;
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

                                int block = world.getBlockId(x, y, z);
                                if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D && (this.replaceID == 0 || block == this.replaceID))
                                {
                                    world.setBlock(x, y, z, this.oreStack.itemID, this.oreStack.getItemDamage(), 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

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
