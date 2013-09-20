package dark.assembly.common;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import org.modstats.ModstatInfo;

import universalelectricity.prefab.TranslationHelper;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.assembly.common.armbot.BlockArmbot;
import dark.assembly.common.armbot.TileEntityArmbot;
import dark.assembly.common.imprinter.BlockImprinter;
import dark.assembly.common.imprinter.ItemImprinter;
import dark.assembly.common.imprinter.TileEntityImprinter;
import dark.assembly.common.machine.BlockCrate;
import dark.assembly.common.machine.BlockDetector;
import dark.assembly.common.machine.BlockManipulator;
import dark.assembly.common.machine.BlockRejector;
import dark.assembly.common.machine.BlockTurntable;
import dark.assembly.common.machine.ItemBlockCrate;
import dark.assembly.common.machine.TileEntityAssembly;
import dark.assembly.common.machine.TileEntityCrate;
import dark.assembly.common.machine.TileEntityDetector;
import dark.assembly.common.machine.TileEntityManipulator;
import dark.assembly.common.machine.TileEntityRejector;
import dark.assembly.common.machine.belt.BlockConveyorBelt;
import dark.assembly.common.machine.belt.TileEntityConveyorBelt;
import dark.assembly.common.machine.encoder.BlockEncoder;
import dark.assembly.common.machine.encoder.ItemDisk;
import dark.assembly.common.machine.encoder.TileEntityEncoder;
import dark.assembly.common.machine.processor.BlockProcessor;
import dark.core.common.DarkMain;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.items.ItemBlockHolder;
import dark.core.registration.ModObjectRegistry;

@ModstatInfo(prefix = "asmline")
@Mod(modid = AssemblyLine.MOD_ID, name = AssemblyLine.MOD_NAME, version = DarkMain.VERSION, dependencies = "required-after:DarkCore", useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class AssemblyLine extends ModPrefab
{

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVIS_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;
    // @Mod
    public static final String MOD_ID = "AssemblyLine";
    public static final String MOD_NAME = "Assembly Line";

    @SidedProxy(clientSide = "dark.assembly.client.ClientProxy", serverSide = "dark.assembly.common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(AssemblyLine.MOD_ID)
    public static AssemblyLine instance;

    public static ALRecipeLoader recipeLoader;

    @Metadata(AssemblyLine.MOD_ID)
    public static ModMetadata meta;

    //public static final String TEXTURE_NAME_PREFIX = "assemblyline:";

    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US", "nl_NL", "fr_FR", "de_DE", "zh_CN" };

    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/AssemblyLine.cfg"));

    public static Logger FMLog = Logger.getLogger(AssemblyLine.MOD_NAME);

    // TODO: MAKE THIS FALSE EVERY BUILD!
    public static final boolean DEBUG = false;
    public static boolean REQUIRE_NO_POWER = false;
    public static boolean VINALLA_RECIPES = false;

    public static Block processorMachine;

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        FMLog.setParent(FMLLog.getLogger());
        instance = this;

        NetworkRegistry.instance().registerGuiHandler(this, proxy);

        //GameRegistry.registerBlock(recipeLoader.blockConveyorBelt, "ConveyorBelt");
        GameRegistry.registerBlock(recipeLoader.blockCrate, ItemBlockCrate.class, "Crate");
        GameRegistry.registerBlock(recipeLoader.blockManipulator, "Manipulator");
        GameRegistry.registerBlock(recipeLoader.blockImprinter, "Imprinter");
        GameRegistry.registerBlock(recipeLoader.blockEncoder, "Encoder");
        GameRegistry.registerBlock(recipeLoader.blockDetector, "Detector");
        GameRegistry.registerBlock(recipeLoader.blockRejector, "Rejector");
        GameRegistry.registerBlock(recipeLoader.blockArmbot, "Armbot");
        GameRegistry.registerBlock(recipeLoader.blockTurntable, "Turntable");
        GameRegistry.registerBlock(recipeLoader.blockCraneController, "CraneController");
        GameRegistry.registerBlock(recipeLoader.blockCraneFrame, "Crane Frame");

        GameRegistry.registerTileEntity(TileEntityConveyorBelt.class, "ALConveyorBelt");
        GameRegistry.registerTileEntity(TileEntityRejector.class, "ALSorter");
        GameRegistry.registerTileEntity(TileEntityManipulator.class, "ALManipulator");
        GameRegistry.registerTileEntity(TileEntityCrate.class, "ALCrate");
        GameRegistry.registerTileEntity(TileEntityDetector.class, "ALDetector");
        GameRegistry.registerTileEntity(TileEntityEncoder.class, "ALEncoder");
        GameRegistry.registerTileEntity(TileEntityArmbot.class, "ALArmbot");
        GameRegistry.registerTileEntity(TileEntityImprinter.class, "ALImprinter");

        TabAssemblyLine.itemStack = new ItemStack(recipeLoader.blockConveyorBelt);

        proxy.preInit();
    }

    @EventHandler
    public void load(FMLInitializationEvent evt)
    {
        super.init(evt);
        proxy.init();

        FMLog.info("Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " languages.");

        recipeLoader.loadRecipes();

    }

    @Override
    public void registerObjects()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new ALRecipeLoader();
        }
        CONFIGURATION.load();
        recipeLoader.blockConveyorBelt = ModObjectRegistry.createNewBlock(AssemblyLine.MOD_ID, BlockConveyorBelt.class);
        recipeLoader.blockManipulator = new BlockManipulator();
        recipeLoader.blockCrate = new BlockCrate();
        recipeLoader.blockImprinter = new BlockImprinter();
        recipeLoader.blockDetector = new BlockDetector();
        recipeLoader.blockRejector = new BlockRejector();
        recipeLoader.blockEncoder = new BlockEncoder();
        recipeLoader.blockArmbot = new BlockArmbot();
        recipeLoader.blockTurntable = new BlockTurntable();
        AssemblyLine.processorMachine = ModObjectRegistry.createNewBlock(AssemblyLine.MOD_ID, BlockProcessor.class, ItemBlockHolder.class);

        recipeLoader.itemImprint = new ItemImprinter(CONFIGURATION.getItem("Imprint", ITEM_ID_PREFIX).getInt());
        recipeLoader.itemDisk = new ItemDisk(CONFIGURATION.getItem("Disk", ITEM_ID_PREFIX + 1).getInt());

        AssemblyLine.VINALLA_RECIPES = CONFIGURATION.get("general", "Vinalla_Recipes", false).getBoolean(false);

        AssemblyLine.REQUIRE_NO_POWER = !CONFIGURATION.get("TileSettings", "requirePower", true).getBoolean(true);
        TileEntityAssembly.refresh_diff = CONFIGURATION.get("TileSettings", "RefreshRandomRange", 9, "n = value of config, 1 + n, random number range from 1 to n that will be added to the lowest refresh value").getInt();
        TileEntityAssembly.refresh_min_rate = CONFIGURATION.get("TileSettings", "RefreshLowestValue", 20, "Lowest value the refresh rate of the tile network will be").getInt();

        CONFIGURATION.save();
    }

    @Override
    public void loadModMeta()
    {
        meta.modId = AssemblyLine.MOD_ID;
        meta.name = AssemblyLine.MOD_NAME;
        meta.description = "Simi Realistic factory system for minecraft bring in conveyor belts, robotic arms, and simple machines";

        meta.url = "http://universalelectricity.com/assembly-line";

        meta.logoFile = "/al_logo.png";
        meta.version = DarkMain.VERSION;
        meta.authorList = Arrays.asList(new String[] { "DarkGuardsman" });
        meta.credits = "Please see the website.";
        meta.autogenerated = false;

    }

    @Override
    public String getDomain()
    {
        return "al";
    }
}
