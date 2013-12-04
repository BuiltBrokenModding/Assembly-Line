package dark.assembly;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

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
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import dark.api.al.coding.TaskRegistry;
import dark.assembly.armbot.BlockArmbot;
import dark.assembly.armbot.command.TaskBreak;
import dark.assembly.armbot.command.TaskDrop;
import dark.assembly.armbot.command.TaskEnd;
import dark.assembly.armbot.command.TaskFire;
import dark.assembly.armbot.command.TaskGOTO;
import dark.assembly.armbot.command.TaskGive;
import dark.assembly.armbot.command.TaskGrabEntity;
import dark.assembly.armbot.command.TaskGrabItem;
import dark.assembly.armbot.command.TaskHarvest;
import dark.assembly.armbot.command.TaskIF;
import dark.assembly.armbot.command.TaskIdle;
import dark.assembly.armbot.command.TaskPlace;
import dark.assembly.armbot.command.TaskReturn;
import dark.assembly.armbot.command.TaskRotateBy;
import dark.assembly.armbot.command.TaskRotateTo;
import dark.assembly.armbot.command.TaskStart;
import dark.assembly.armbot.command.TaskTake;
import dark.assembly.armbot.command.TaskUse;
import dark.assembly.imprinter.BlockImprinter;
import dark.assembly.imprinter.ItemImprinter;
import dark.assembly.machine.BlockCrate;
import dark.assembly.machine.BlockDetector;
import dark.assembly.machine.BlockManipulator;
import dark.assembly.machine.BlockRejector;
import dark.assembly.machine.BlockTurntable;
import dark.assembly.machine.ItemBlockCrate;
import dark.assembly.machine.TileEntityAssembly;
import dark.assembly.machine.belt.BlockConveyorBelt;
import dark.assembly.machine.encoder.BlockEncoder;
import dark.assembly.machine.encoder.ItemDisk;
import dark.assembly.machine.processor.BlockProcessor;
import dark.assembly.machine.red.BlockAdvancedHopper;
import dark.core.ModObjectRegistry;
import dark.core.prefab.ItemBlockHolder;
import dark.core.prefab.ModPrefab;
import dark.machines.common.DMCreativeTab;
import dark.machines.common.DarkMain;

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

    @SidedProxy(clientSide = "dark.assembly.client.ClientProxy", serverSide = "dark.assembly.CommonProxy")
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

    public static boolean VINALLA_RECIPES = false;

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        FMLog.setParent(FMLLog.getLogger());
        instance = this;

        NetworkRegistry.instance().registerGuiHandler(this, proxy);

        proxy.preInit();

        TaskRegistry.registerCommand(new TaskDrop());
        TaskRegistry.registerCommand(new TaskGive());
        TaskRegistry.registerCommand(new TaskTake());
        TaskRegistry.registerCommand(new TaskGrabItem());
        TaskRegistry.registerCommand(new TaskGrabEntity());
        TaskRegistry.registerCommand(new TaskRotateBy());
        TaskRegistry.registerCommand(new TaskRotateTo());
        TaskRegistry.registerCommand(new TaskUse());
        TaskRegistry.registerCommand(new TaskIF());
        TaskRegistry.registerCommand(new TaskGOTO());
        TaskRegistry.registerCommand(new TaskReturn());
        TaskRegistry.registerCommand(new TaskEnd());
        TaskRegistry.registerCommand(new TaskFire());
        TaskRegistry.registerCommand(new TaskHarvest());
        TaskRegistry.registerCommand(new TaskPlace());
        TaskRegistry.registerCommand(new TaskBreak());
        TaskRegistry.registerCommand(new TaskStart());
        TaskRegistry.registerCommand(new TaskIdle());
    }

    @Override
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        proxy.init();

        FMLog.info("Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " languages.");
        DMCreativeTab.tabAutomation.setIconItemStack(new ItemStack(ALRecipeLoader.blockConveyorBelt));
    }

    @Override
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        proxy.postInit();
    }

    @Override
    public void registerObjects()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new ALRecipeLoader();
        }
        CONFIGURATION.load();
        ALRecipeLoader.blockConveyorBelt = ModObjectRegistry.createNewBlock("ALBlockConveyor", AssemblyLine.MOD_ID, BlockConveyorBelt.class);
        ALRecipeLoader.blockManipulator = ModObjectRegistry.createNewBlock("Manipulator", AssemblyLine.MOD_ID, BlockManipulator.class);
        ALRecipeLoader.blockCrate = (BlockCrate) ModObjectRegistry.createNewBlock("Crate", AssemblyLine.MOD_ID, BlockCrate.class, ItemBlockCrate.class);
        ALRecipeLoader.blockImprinter = ModObjectRegistry.createNewBlock("Imprinter", AssemblyLine.MOD_ID, BlockImprinter.class);
        ALRecipeLoader.blockDetector = ModObjectRegistry.createNewBlock("Detector", AssemblyLine.MOD_ID, BlockDetector.class);
        ALRecipeLoader.blockRejector = ModObjectRegistry.createNewBlock("Rejector", AssemblyLine.MOD_ID, BlockRejector.class);
        ALRecipeLoader.blockEncoder = ModObjectRegistry.createNewBlock("Encoder", AssemblyLine.MOD_ID, BlockEncoder.class);
        ALRecipeLoader.blockArmbot = ModObjectRegistry.createNewBlock("Armbot", AssemblyLine.MOD_ID, BlockArmbot.class);
        ALRecipeLoader.blockTurntable = ModObjectRegistry.createNewBlock("Turntable", AssemblyLine.MOD_ID, BlockTurntable.class);
        ALRecipeLoader.processorMachine = ModObjectRegistry.createNewBlock("ALBlockProcessor", AssemblyLine.MOD_ID, BlockProcessor.class, ItemBlockHolder.class);
        ALRecipeLoader.blockAdvancedHopper = ModObjectRegistry.createNewBlock("ALBlockHopper", AssemblyLine.MOD_ID, BlockAdvancedHopper.class, ItemBlockHolder.class);

        ALRecipeLoader.itemImprint = new ItemImprinter(CONFIGURATION.getItem("Imprint", ITEM_ID_PREFIX).getInt());
        ALRecipeLoader.itemDisk = new ItemDisk(CONFIGURATION.getItem("Disk", ITEM_ID_PREFIX + 1).getInt());

        AssemblyLine.VINALLA_RECIPES = CONFIGURATION.get("general", "Vinalla_Recipes", false).getBoolean(false);

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

        meta.url = "http://www.universalelectricity.com/coremachine";

        meta.logoFile = "/al_logo.png";
        meta.version = DarkMain.VERSION;
        meta.authorList = Arrays.asList(new String[] { "DarkGuardsman", "Briman", "Calclavia" });
        meta.credits = "Please see the website.";
        meta.autogenerated = false;

    }

    @Override
    public String getDomain()
    {
        return "al";
    }

    @Override
    public void loadRecipes()
    {
        recipeLoader.loadRecipes();
    }
}
