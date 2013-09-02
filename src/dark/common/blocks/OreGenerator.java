package dark.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class OreGenerator implements IWorldGenerator
{
    public static boolean isInitiated = false;

    /** Add your ore data to this list of ores for it to automatically generate! No hassle indeed! */
    private static final List<OreGenSettings> ORES_TO_GENERATE = new ArrayList<OreGenSettings>();

    /** Adds an ore to the ore generate list. Do this in pre-init. */
    public static void addOre(OreGenSettings data)
    {
        if (!isInitiated)
        {
            GameRegistry.registerWorldGenerator(new OreGenerator());
        }

        ORES_TO_GENERATE.add(data);
    }

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        chunkX = chunkX << 4;
        chunkZ = chunkZ << 4;

        // Checks to make sure this is the normal
        // world
        for (OreGenSettings oreData : ORES_TO_GENERATE)
        {
            if (oreData.shouldGenerate && oreData.isOreGeneratedInWorld(world, chunkGenerator))
            {
                oreData.generate(world, rand, chunkX, chunkZ);
            }

        }
    }
}
