package dark.assembly.common;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import org.modstats.ModstatInfo;

import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
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
import dark.assembly.common.machine.TileEntityCrate;
import dark.assembly.common.machine.TileEntityDetector;
import dark.assembly.common.machine.TileEntityManipulator;
import dark.assembly.common.machine.TileEntityRejector;
import dark.assembly.common.machine.belt.BlockConveyorBelt;
import dark.assembly.common.machine.belt.TileEntityConveyorBelt;
import dark.assembly.common.machine.crane.BlockCraneController;
import dark.assembly.common.machine.crane.BlockCraneFrame;
import dark.assembly.common.machine.crane.TileEntityCraneController;
import dark.assembly.common.machine.crane.TileEntityCraneRail;
import dark.assembly.common.machine.encoder.BlockEncoder;
import dark.assembly.common.machine.encoder.ItemDisk;
import dark.assembly.common.machine.encoder.TileEntityEncoder;
import dark.core.DarkMain;
import dark.core.ModPrefab;

@ModstatInfo(prefix = "asmline")
@Mod(modid = AssemblyLine.CHANNEL, name = AssemblyLine.MOD_NAME, version = DarkMain.VERSION, dependencies = "after:DarkCore", useMetadata = true)
@NetworkMod(channels = { AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class AssemblyLine extends ModPrefab
{

    // @Mod
    public static final String MOD_ID = "AssemblyLine";
    public static final String MOD_NAME = "Assembly Line";

    // @NetworkMod
    public static final String CHANNEL = "AssemblyLine";

    @SidedProxy(clientSide = "dark.assembly.client.ClientProxy", serverSide = "dark.assembly.common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(AssemblyLine.CHANNEL)
    public static AssemblyLine instance;

    public static ALRecipeLoader recipeLoader;

    @Metadata(AssemblyLine.MOD_ID)
    public static ModMetadata meta;

    //public static final String TEXTURE_NAME_PREFIX = "assemblyline:";

    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US", "nl_NL", "fr_FR", "de_DE" };

    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/AssemblyLine.cfg"));

    public static Logger FMLog = Logger.getLogger(AssemblyLine.MOD_NAME);

    // TODO: MAKE THIS FALSE EVERY BUILD!
    public static final boolean DEBUG = false;
    public static boolean REQUIRE_NO_POWER = false;
    public static boolean VINALLA_RECIPES = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        FMLog.setParent(FMLLog.getLogger());
        instance = this;

        NetworkRegistry.instance().registerGuiHandler(this, proxy);

        GameRegistry.registerBlock(recipeLoader.blockConveyorBelt, "ConveyorBelt");
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
        GameRegistry.registerTileEntity(TileEntityCraneController.class, "ALCraneController");
        GameRegistry.registerTileEntity(TileEntityCraneRail.class, "ALCraneRail");
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
    public void loadConfig()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new ALRecipeLoader();
        }
        CONFIGURATION.load();
        recipeLoader.blockConveyorBelt = new BlockConveyorBelt(getNextID());
        recipeLoader.blockManipulator = new BlockManipulator(getNextID());
        recipeLoader.blockCrate = new BlockCrate(getNextID());
        recipeLoader.blockImprinter = new BlockImprinter(getNextID());
        recipeLoader.blockDetector = new BlockDetector(getNextID());
        recipeLoader.blockRejector = new BlockRejector(getNextID());
        recipeLoader.blockEncoder = new BlockEncoder(getNextID());
        recipeLoader.blockArmbot = new BlockArmbot(getNextID());
        recipeLoader.blockCraneController = new BlockCraneController(getNextID());
        recipeLoader.blockCraneFrame = new BlockCraneFrame(getNextID());
        recipeLoader.blockTurntable = new BlockTurntable(getNextID());

        recipeLoader.itemImprint = new ItemImprinter(CONFIGURATION.getItem("Imprint", ITEM_ID_PREFIX).getInt());
        recipeLoader.itemDisk = new ItemDisk(CONFIGURATION.getItem("Disk", ITEM_ID_PREFIX + 1).getInt());

        AssemblyLine.REQUIRE_NO_POWER = !CONFIGURATION.get("general", "requirePower", true).getBoolean(true);
        AssemblyLine.VINALLA_RECIPES = CONFIGURATION.get("general", "Vinalla_Recipes", false).getBoolean(false);

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
