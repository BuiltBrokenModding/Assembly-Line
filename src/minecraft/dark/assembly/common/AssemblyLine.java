package dark.assembly.common;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.FMLCommonHandler;
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
import cpw.mods.fml.relauncher.Side;
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
import dark.core.blocks.BlockMulti;
import dark.core.blocks.TileEntityMulti;

@ModstatInfo(prefix = "asmline")
@Mod(modid = AssemblyLine.CHANNEL, name = AssemblyLine.MOD_NAME, version = DarkMain.VERSION, dependencies = "after:DarkCore", useMetadata = true)
@NetworkMod(channels = { AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class AssemblyLine extends ModPrefab
{
	public AssemblyLine()
	{
		super("al");
		// TODO Auto-generated constructor stub
	}

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
		recipeLoader = new ALRecipeLoader();

		/* UPDATE NOTIFIER */
		Modstats.instance().getReporter().registerMod(this);

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

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

	public static void printSidedData(String data)
	{
		System.out.print(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? "[C]" : "[S]");
		System.out.println(" " + data);
	}

	@Override
	public void loadConfig()
	{
		CONFIGURATION.load();
		recipeLoader.blockConveyorBelt = new BlockConveyorBelt(BLOCK_ID_PREFIX);
		recipeLoader.blockManipulator = new BlockManipulator(BLOCK_ID_PREFIX++);
		recipeLoader.blockCrate = new BlockCrate(BLOCK_ID_PREFIX++);
		recipeLoader.blockImprinter = new BlockImprinter(BLOCK_ID_PREFIX++);
		recipeLoader.blockDetector = new BlockDetector(BLOCK_ID_PREFIX++);
		recipeLoader.blockRejector = new BlockRejector(BLOCK_ID_PREFIX++);
		recipeLoader.blockEncoder = new BlockEncoder(BLOCK_ID_PREFIX++);
		recipeLoader.blockArmbot = new BlockArmbot(BLOCK_ID_PREFIX++);
		recipeLoader.blockCraneController = new BlockCraneController(BLOCK_ID_PREFIX++);
		recipeLoader.blockCraneFrame = new BlockCraneFrame(BLOCK_ID_PREFIX++);
		recipeLoader.blockTurntable = new BlockTurntable(BLOCK_ID_PREFIX++);

		recipeLoader.itemImprint = new ItemImprinter(CONFIGURATION.getItem("Imprint", ITEM_ID_PREFIX).getInt());
		recipeLoader.itemDisk = new ItemDisk(CONFIGURATION.getItem("Disk", ITEM_ID_PREFIX + 1).getInt());

		AssemblyLine.REQUIRE_NO_POWER = !CONFIGURATION.get("general", "requirePower", true).getBoolean(true);
		AssemblyLine.VINALLA_RECIPES = CONFIGURATION.get("general", "Vinalla_Recipes", false).getBoolean(false);
		if (CONFIGURATION.hasChanged())
		{
			CONFIGURATION.save();
		}

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
}
