package dark.core.common;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.compatibility.Compatibility;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import dark.api.ProcessorRecipes;
import dark.core.common.blocks.BlockBasalt;
import dark.core.common.blocks.BlockColorGlass;
import dark.core.common.blocks.BlockColorGlowGlass;
import dark.core.common.blocks.BlockColorSand;
import dark.core.common.blocks.BlockOre;
import dark.core.common.blocks.BlockOre.OreData;
import dark.core.common.blocks.ItemBlockColored;
import dark.core.common.blocks.ItemBlockOre;
import dark.core.common.debug.BlockDebug;
import dark.core.common.items.EnumMaterial;
import dark.core.common.items.ItemBattery;
import dark.core.common.items.ItemColoredDust;
import dark.core.common.items.ItemOreDirv;
import dark.core.common.items.ItemParts;
import dark.core.common.items.ItemParts.Parts;
import dark.core.common.items.ItemTools;
import dark.core.common.items.ItemWrench;
import dark.core.common.machines.BlockBasicMachine;
import dark.core.common.machines.BlockSolarPanel;
import dark.core.common.transmit.BlockWire;
import dark.core.network.PacketHandler;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.items.ItemBlockHolder;
import dark.core.prefab.machine.BlockMulti;
import dark.core.registration.ModObjectRegistry;

/** @author HangCow, DarkGuardsman */
@Mod(modid = DarkMain.MOD_ID, name = DarkMain.MOD_NAME, version = DarkMain.VERSION, dependencies = "after:BuildCraft|Energy", useMetadata = true)
@NetworkMod(channels = { DarkMain.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class DarkMain extends ModPrefab
{
    // @Mod Prerequisites
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVIS_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;

    // @Mod
    public static final String MOD_ID = "DarkCore";
    public static final String MOD_NAME = "Darks CoreMachine";

    @SidedProxy(clientSide = "dark.core.client.ClientProxy", serverSide = "dark.core.common.CommonProxy")
    public static CommonProxy proxy;

    public static final String CHANNEL = "DarkPackets";

    @Metadata(DarkMain.MOD_ID)
    public static ModMetadata meta;

    /** Main config file */
    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/TheDarkMachine.cfg"));
    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };
    /** Can over pressure of devices do area damage */
    public static boolean overPressureDamage;

    public static BlockMulti blockMulti;

    @Instance(MOD_ID)
    private static DarkMain instance;

    public static CoreRecipeLoader recipeLoader;

    public static final String[] dyeColorNames = new String[] { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "Silver", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White" };
    public static final Color[] dyeColors = new Color[] { Color.black, Color.red, Color.green, new Color(139, 69, 19), Color.BLUE, new Color(75, 0, 130), Color.cyan, new Color(192, 192, 192), Color.gray, Color.pink, new Color(0, 255, 0), Color.yellow, new Color(135, 206, 250), Color.magenta, Color.orange, Color.white };

    public static DarkMain getInstance()
    {
        if (instance == null)
        {
            instance = new DarkMain();
        }
        return instance;
    }

    @EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new FluidHelper());

        UniversalElectricity.initiate();
        Compatibility.initiate();
        NetworkRegistry.instance().registerGuiHandler(this, proxy);
        proxy.preInit();
    }

    @EventHandler
    @Override
    public void init(FMLInitializationEvent event)
    {
        ExternalModHandler.init();
        super.init(event);
        ProcessorRecipes.parseOreNames();
        if (CoreRecipeLoader.blockOre != null)
        {
            for (OreData data : OreData.values())
            {
                if (data.doWorldGen)
                {
                    OreGenReplaceStone gen = data.getGeneratorSettings();
                    if (gen != null)
                    {
                        OreGenerator.addOre(gen);
                    }
                }
            }
        }
        if (CoreRecipeLoader.itemParts != null)
        {
            /* ORE DIRECTORY REGISTER */
            OreDictionary.registerOre("bronzeTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Bronze.ordinal()));
            OreDictionary.registerOre("ironTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Iron.ordinal()));
            OreDictionary.registerOre("netherTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Nether.ordinal()));
            OreDictionary.registerOre("obbyTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Obby.ordinal()));
            OreDictionary.registerOre("leatherSeal", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Seal.ordinal()));
            OreDictionary.registerOre("leatherSlimeSeal", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.SlimeSeal.ordinal()));
            OreDictionary.registerOre("valvePart", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()));
            OreDictionary.registerOre("bronzeTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Bronze.ordinal()));
            OreDictionary.registerOre("unfinishedTank", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Tank.ordinal()));

        }
        FMLLog.info(" Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");
        proxy.init();
    }

    @EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        //TODO load langs
        recipeLoader.loadRecipes();

        proxy.postInit();

    }

    @Override
    public void registerObjects()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new CoreRecipeLoader();
        }
        /* CONFIGS */
        CONFIGURATION.load();
        /* BLOCKS */
        Block m = ModObjectRegistry.createNewBlock("DMBlockMulti", DarkMain.MOD_ID, BlockMulti.class, false);
        if (m instanceof BlockMulti)
        {
            blockMulti = (BlockMulti) m;
        }
        CoreRecipeLoader.basicMachine = ModObjectRegistry.createNewBlock("DMBlockBasicMachine", DarkMain.MOD_ID, BlockBasicMachine.class, ItemBlockHolder.class);
        CoreRecipeLoader.blockOre = ModObjectRegistry.createNewBlock("DMBlockOre", DarkMain.MOD_ID, BlockOre.class, ItemBlockOre.class);
        CoreRecipeLoader.blockWire = ModObjectRegistry.createNewBlock("DMBlockWire", DarkMain.MOD_ID, BlockWire.class, ItemBlockHolder.class);
        CoreRecipeLoader.blockDebug = ModObjectRegistry.createNewBlock("DMBlockDebug", DarkMain.MOD_ID, BlockDebug.class, ItemBlockHolder.class);
        CoreRecipeLoader.blockStainGlass = ModObjectRegistry.createNewBlock("DMBlockStainedGlass", DarkMain.MOD_ID, BlockColorGlass.class, ItemBlockColored.class);
        CoreRecipeLoader.blockColorSand = ModObjectRegistry.createNewBlock("DMBlockColorSand", DarkMain.MOD_ID, BlockColorSand.class, ItemBlockColored.class);
        CoreRecipeLoader.blockBasalt = ModObjectRegistry.createNewBlock("DMBlockBasalt", DarkMain.MOD_ID, BlockBasalt.class, ItemBlockColored.class);
        CoreRecipeLoader.blockGlowGlass = ModObjectRegistry.createNewBlock("DMBlockGlowGlass", DarkMain.MOD_ID, BlockColorGlowGlass.class, ItemBlockColored.class);
        CoreRecipeLoader.blockSolar = ModObjectRegistry.createNewBlock("DMBlockSolar", DarkMain.MOD_ID, BlockSolarPanel.class, ItemBlockHolder.class);

        /* ITEMS */
        CoreRecipeLoader.itemTool = new ItemTools(ITEM_ID_PREFIX++, DarkMain.CONFIGURATION);

        if (CONFIGURATION.get("general", "LoadOreItems", true, "Only disable ore items if you have another mod that provides metal dust, ingots, and plates").getBoolean(true))
        {
            CoreRecipeLoader.itemMetals = new ItemOreDirv(ITEM_ID_PREFIX++, CONFIGURATION);
        }
        if (CONFIGURATION.get("general", "EnableBattery", true).getBoolean(true))
        {
            CoreRecipeLoader.battery = new ItemBattery("Battery", ITEM_ID_PREFIX++);
        }
        if (CONFIGURATION.get("general", "EnableWrench", true).getBoolean(true))
        {
            CoreRecipeLoader.wrench = new ItemWrench(ITEM_ID_PREFIX++, DarkMain.CONFIGURATION);
        }
        if (CONFIGURATION.get("general", "LoadCraftingParts", true, "Only disable this if you do not plan to craft, or are not using any mods that need these parts.").getBoolean(true))
        {
            CoreRecipeLoader.itemParts = new ItemParts(ITEM_ID_PREFIX++, CONFIGURATION);
            CoreRecipeLoader.itemGlowingSand = new ItemColoredDust(CONFIGURATION.getItem(Configuration.CATEGORY_ITEM, "GlowingRefinedSandItemID", ITEM_ID_PREFIX++).getInt(), "GlowRefinedSand");
        }
        CONFIGURATION.save();
    }

    @Override
    public void loadModMeta()
    {
        /* MCMOD.INFO FILE BUILDER? */
        meta.modId = MOD_ID;
        meta.name = MOD_NAME;
        meta.description = "Main mod for several of the mods created by DarkGuardsman and his team. Adds basic features, functions, ores, items, and blocks";
        meta.url = "www.BuiltBroken.com";

        meta.logoFile = TEXTURE_DIRECTORY + "GP_Banner.png";
        meta.version = VERSION;
        meta.authorList = Arrays.asList(new String[] { "DarkGuardsman", "HangCow" });
        meta.credits = "Please see the website.";
        meta.autogenerated = false;
    }

    @ForgeSubscribe
    public void onWorldSave(WorldEvent.Save event)
    {
        //SaveManager.save(!event.world.isRemote);
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        //SaveManager.save(true);
    }

    @Override
    public String getDomain()
    {
        return "dark";
    }

}
