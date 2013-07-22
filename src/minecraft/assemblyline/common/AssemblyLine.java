package assemblyline.common;

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
import assemblyline.common.armbot.BlockArmbot;
import assemblyline.common.armbot.TileEntityArmbot;
import assemblyline.common.imprinter.BlockImprinter;
import assemblyline.common.imprinter.ItemImprinter;
import assemblyline.common.imprinter.TileEntityImprinter;
import assemblyline.common.machine.BlockCrate;
import assemblyline.common.machine.BlockManipulator;
import assemblyline.common.machine.BlockRejector;
import assemblyline.common.machine.BlockTurntable;
import assemblyline.common.machine.ItemBlockCrate;
import assemblyline.common.machine.TileEntityCrate;
import assemblyline.common.machine.TileEntityManipulator;
import assemblyline.common.machine.TileEntityRejector;
import assemblyline.common.machine.belt.BlockConveyorBelt;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;
import assemblyline.common.machine.crane.BlockCraneController;
import assemblyline.common.machine.crane.BlockCraneFrame;
import assemblyline.common.machine.crane.TileEntityCraneController;
import assemblyline.common.machine.crane.TileEntityCraneRail;
import assemblyline.common.machine.detector.BlockDetector;
import assemblyline.common.machine.detector.TileEntityDetector;
import assemblyline.common.machine.encoder.BlockEncoder;
import assemblyline.common.machine.encoder.ItemDisk;
import assemblyline.common.machine.encoder.TileEntityEncoder;
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
import dark.core.DarkMain;
import dark.core.ModPrefab;
import dark.prefab.machine.BlockMulti;
import dark.prefab.machine.TileEntityMulti;

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

	@SidedProxy(clientSide = "assemblyline.client.ClientProxy", serverSide = "assemblyline.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(AssemblyLine.CHANNEL)
	public static AssemblyLine instance;

	@Metadata(AssemblyLine.MOD_ID)
	public static ModMetadata meta;

	//public static final String TEXTURE_NAME_PREFIX = "assemblyline:";

	private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US", "nl_NL", "fr_FR", "de_DE" };

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/AssemblyLine.cfg"));


	public static Block blockConveyorBelt;
	public static Block blockManipulator;
	public static BlockCrate blockCrate;
	public static Block blockImprinter;
	public static Block blockEncoder;
	public static Block blockDetector;
	public static Block blockRejector;
	public static Block blockArmbot;
	public static Block blockCraneController;
	public static Block blockCraneFrame;
	public static Block blockTurntable;

	public static Item itemImprint;
	public static Item itemDisk;

	public static Logger FMLog = Logger.getLogger(AssemblyLine.MOD_NAME);

	// TODO: MAKE THIS FALSE EVERY BUILD!
	public static final boolean DEBUG = false;
	public static boolean REQUIRE_NO_POWER = false;
	public static boolean VINALLA_RECIPES = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		FMLog.setParent(FMLLog.getLogger());
		instance = this;

		/* UPDATE NOTIFIER */
		Modstats.instance().getReporter().registerMod(this);

		CONFIGURATION.load();
		blockConveyorBelt = new BlockConveyorBelt(BLOCK_ID_PREFIX);
		blockManipulator = new BlockManipulator(BLOCK_ID_PREFIX++);
		blockCrate = new BlockCrate(BLOCK_ID_PREFIX++);
		blockImprinter = new BlockImprinter(BLOCK_ID_PREFIX++);
		blockDetector = new BlockDetector(BLOCK_ID_PREFIX++);
		blockRejector = new BlockRejector(BLOCK_ID_PREFIX++);
		blockEncoder = new BlockEncoder(BLOCK_ID_PREFIX++);
		blockArmbot = new BlockArmbot(BLOCK_ID_PREFIX++);
		blockCraneController = new BlockCraneController(BLOCK_ID_PREFIX++);
		blockCraneFrame = new BlockCraneFrame(BLOCK_ID_PREFIX++);
		blockTurntable = new BlockTurntable(BLOCK_ID_PREFIX++);

		itemImprint = new ItemImprinter(CONFIGURATION.getItem("Imprint", ITEM_ID_PREFIX).getInt());
		itemDisk = new ItemDisk(CONFIGURATION.getItem("Disk", ITEM_ID_PREFIX + 1).getInt());

		AssemblyLine.REQUIRE_NO_POWER = !CONFIGURATION.get("general", "requirePower", true).getBoolean(true);
		AssemblyLine.VINALLA_RECIPES = CONFIGURATION.get("general", "Vinalla_Recipes", false).getBoolean(false);
		if (CONFIGURATION.hasChanged())
		{
			CONFIGURATION.save();
		}

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

		GameRegistry.registerBlock(blockConveyorBelt, "ConveyorBelt");
		GameRegistry.registerBlock(blockCrate, ItemBlockCrate.class, "Crate");
		GameRegistry.registerBlock(blockManipulator, "Manipulator");
		GameRegistry.registerBlock(blockImprinter, "Imprinter");
		GameRegistry.registerBlock(blockEncoder, "Encoder");
		GameRegistry.registerBlock(blockDetector, "Detector");
		GameRegistry.registerBlock(blockRejector, "Rejector");
		GameRegistry.registerBlock(blockArmbot, "Armbot");
		GameRegistry.registerBlock(blockTurntable, "Turntable");
		GameRegistry.registerBlock(blockCraneController, "CraneController");
		GameRegistry.registerBlock(blockCraneFrame, "Crane Frame");

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

		TabAssemblyLine.itemStack = new ItemStack(AssemblyLine.blockConveyorBelt);

		proxy.preInit();
	}

	@EventHandler
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();

		FMLog.info("Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " languages.");

		/* MCMOD.INFO FILE BUILDER? */
		meta.modId = AssemblyLine.MOD_ID;
		meta.name = AssemblyLine.MOD_NAME;
		meta.description = "Simi Realistic factory system for minecraft bring in conveyor belts, robotic arms, and simple machines";

		meta.url = "http://universalelectricity.com/assembly-line";

		meta.logoFile = "/al_logo.png";
		meta.version = DarkMain.VERSION;
		meta.authorList = Arrays.asList(new String[] { "DarkGuardsman" });
		meta.credits = "Please see the website.";
		meta.autogenerated = false;

		Recipes.loadRecipes();

	}

	public static void printSidedData(String data)
	{
		System.out.print(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? "[C]" : "[S]");
		System.out.println(" " + data);
	}

	@Override
	public void loadConfig()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void loadModMeta()
	{
		// TODO Auto-generated method stub

	}
}
