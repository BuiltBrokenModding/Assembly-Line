package dark.illus;

import java.awt.Color;
import java.io.File;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.illus.blocks.colored.BlockBasalt;
import dark.illus.blocks.colored.BlockColorGlass;
import dark.illus.blocks.colored.BlockColorSand;
import dark.illus.blocks.colored.ItemBlockBasalt;
import dark.illus.blocks.colored.ItemBlockColored;
import dark.illus.items.ItemColored;

@Mod(modid = IllustriousElements.MOD_ID, name = IllustriousElements.MOD_NAME, version = IllustriousElements.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class IllustriousElements
{
    // @Mod Prerequisites
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVIS_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";

    // @Mod
    public static final String MOD_ID = "Illustrious_Elements";
    public static final String MOD_NAME = "Illustrious_Elements";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;
    @Instance(IllustriousElements.MOD_ID)
    public static IllustriousElements Instance;

    @SidedProxy(clientSide = "dark.illus.ClientProxy", serverSide = "dark.illus.CommonProxy")
    public static CommonProxy proxy;

    public static final String RESOURCE_PATH = "/mods/illus/";

    public static final String TEXTURE_DIRECTORY = RESOURCE_PATH + "textures/";
    public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";
    public static final String BLOCK_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
    public static final String ITEM_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "items/";
    public static final String MODEL_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "models/";
    public static final String LANGUAGE_PATH = RESOURCE_PATH + "lang/";

    public static final String TEXTURE_NAME_PREFIX = "illus:";

    // Variables //
    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };

    static Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), "IllustriousElements.cfg"));

    public static Logger FMLog = Logger.getLogger(MOD_NAME);

    public static final String[] dyeColorNames = new String[] { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "Silver", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White" };
    public static final Color[] dyeColors = new Color[] { Color.black, Color.red, Color.green, new Color(139, 69, 19), Color.BLUE, new Color(75, 0, 130), Color.cyan, new Color(192, 192, 192), Color.gray, Color.pink, new Color(0, 255, 0), Color.yellow, new Color(135, 206, 250), Color.magenta, Color.orange, Color.white };
    // Blocks //
    public static Block blockStainGlass;
    public static Block blockColorSand;
    public static Block blockBasalt;
    public static Block blockGlowGlass;

    // Item //
    public static Item itemRefinedSand;
    public static Item itemGlowingSand;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();

        // // Configuration ////
        config.load();
        blockStainGlass = new BlockColorGlass(config.getBlock(Configuration.CATEGORY_BLOCK, "StainedGlassBlockID", 1200).getInt(), "StainedGlass");
        blockColorSand = new BlockColorSand(config.getBlock(Configuration.CATEGORY_BLOCK, "ColoredSandBlockID", 1201).getInt());
        blockBasalt = new BlockBasalt(config.getBlock(Configuration.CATEGORY_BLOCK, "ExtraBlocksBlockID", 1202).getInt());
        blockGlowGlass = new BlockColorGlass(config.getBlock(Configuration.CATEGORY_BLOCK, "GlowingGlassBlockID", 1203).getInt(), "GlowGlass").setLightOpacity(2).setLightValue(1);

        itemRefinedSand = new ItemColored(config.getItem(Configuration.CATEGORY_ITEM, "RefinedSandItemID", 30010).getInt(), "RefinedSand");
        itemGlowingSand = new ItemColored(config.getItem(Configuration.CATEGORY_ITEM, "GlowingRefinedSandItemID", 30020).getInt(), "GlowRefinedSand");
        config.save();

        // // Registration ////
        GameRegistry.registerBlock(blockStainGlass, ItemBlockColored.class, "stainGlass");
        GameRegistry.registerBlock(blockColorSand, ItemBlockColored.class, "stainSand");
        GameRegistry.registerBlock(blockBasalt, ItemBlockBasalt.class, "extraBlocks");
        GameRegistry.registerBlock(blockGlowGlass, ItemBlockColored.class, "stainGlowGlass");

    }

    @EventHandler
    public void generalLoad(FMLInitializationEvent event)
    {
        proxy.init();
        // // Block Names ////
        FMLog.info(" Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {
        proxy.postInit();
        // // Block Recipes ////

        // Stained Glass //
        for (int i = 0; i < dyeColorNames.length; i++)
        {
            FurnaceRecipes.smelting().addSmelting(IllustriousElements.itemRefinedSand.itemID, i, new ItemStack(IllustriousElements.blockStainGlass, 1, i), 10F);
        }

        // Glowing Glass //
        for (int i = 0; i < dyeColorNames.length; i++)
        {
            FurnaceRecipes.smelting().addSmelting(IllustriousElements.itemGlowingSand.itemID, i, new ItemStack(IllustriousElements.blockGlowGlass, 1, i), 10F);
        }

        // Colored Sand //
        for (int j = 0; j < dyeColorNames.length; j++)
        {
            GameRegistry.addRecipe(new ItemStack(blockColorSand, 8, j), new Object[] { "SSS", "SDS", "SSS", 'S', Block.sand, 'D', new ItemStack(Item.dyePowder, 1, j) });
        }

        // Extra Block //

        GameRegistry.addShapelessRecipe(new ItemStack(blockBasalt, 1, BlockBasalt.block.COBBLE.ordinal()), new Object[] { new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()) });
        GameRegistry.addShapelessRecipe(new ItemStack(blockBasalt, 1, BlockBasalt.block.MOSSY.ordinal()), new Object[] { new ItemStack(blockBasalt, 1, BlockBasalt.block.BRICK.ordinal()), Block.vine });
        GameRegistry.addShapelessRecipe(new ItemStack(blockBasalt, 2, BlockBasalt.block.CRACKED.ordinal()), new Object[] { new ItemStack(blockBasalt, 1, BlockBasalt.block.BRICK.ordinal()), new ItemStack(blockBasalt, 1, BlockBasalt.block.BRICK.ordinal()) });
        GameRegistry.addRecipe(new ItemStack(blockBasalt, 4, BlockBasalt.block.BRICK.ordinal()), new Object[] { "SS", "SS", 'S', new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()) });
        GameRegistry.addRecipe(new ItemStack(blockBasalt, 8, BlockBasalt.block.CHISILED.ordinal()), new Object[] { "SSS", "S S", "SSS", 'S', new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()) });

        GameRegistry.addShapelessRecipe(new ItemStack(blockBasalt, 2, BlockBasalt.block.COBBLE.ordinal()), new Object[] { Block.cobblestone, new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()) });
        GameRegistry.addSmelting(Block.stone.blockID, new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()), 1f);

        // // Item Recipes ////

        // Refined Sand //
        for (int j = 0; j < dyeColorNames.length; j++)
        {
            GameRegistry.addShapelessRecipe(new ItemStack(itemRefinedSand, 1, j), new Object[] { new ItemStack(blockColorSand, 1, j) });
        }

        // Glowing Refined Sand //
        for (int j = 0; j < dyeColorNames.length; j++)
        {
            GameRegistry.addShapelessRecipe(new ItemStack(itemGlowingSand, 1, j), new Object[] { new ItemStack(itemRefinedSand, 1, j), Item.redstone });
        }
    }
}