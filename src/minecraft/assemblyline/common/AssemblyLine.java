package assemblyline.common;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.multiblock.BlockMulti;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.armbot.BlockArmbot;
import assemblyline.common.armbot.TileEntityArmbot;
import assemblyline.common.armbot.command.GrabDictionary;
import assemblyline.common.block.BlockCrate;
import assemblyline.common.block.BlockTurntable;
import assemblyline.common.block.ItemBlockCrate;
import assemblyline.common.block.TileEntityCrate;
import assemblyline.common.imprinter.BlockImprinter;
import assemblyline.common.imprinter.ItemImprinter;
import assemblyline.common.imprinter.TileEntityImprinter;
import assemblyline.common.machine.BlockManipulator;
import assemblyline.common.machine.BlockRejector;
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
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dark.core.DarkMain;
import dark.core.PowerSystems;

@ModstatInfo(prefix = "asmline")
@Mod(modid = AssemblyLine.CHANNEL, name = AssemblyLine.MOD_NAME, version = AssemblyLine.VERSION, dependencies = "after:BasicComponents; after:IC2", useMetadata = true)
@NetworkMod(channels = { AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class AssemblyLine
{

	// @Mod Prerequisites
	public static final String MAJOR_VERSION = "@MAJOR@";
	public static final String MINOR_VERSION = "@MINOR@";
	public static final String REVIS_VERSION = "@REVIS@";
	public static final String BUILD_VERSION = "@BUILD@";

	// @Mod
	public static final String MOD_ID = "AssemblyLine";
	public static final String MOD_NAME = "Assembly Line";
	public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;

	// @NetworkMod
	public static final String CHANNEL = "AssemblyLine";

	@SidedProxy(clientSide = "assemblyline.client.ClientProxy", serverSide = "assemblyline.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(AssemblyLine.CHANNEL)
	public static AssemblyLine instance;

	@Metadata(AssemblyLine.MOD_ID)
	public static ModMetadata meta;

	public static final String DIRECTORY_NO_SLASH = "mods/assemblyline/";
	public static final String DIRECTORY = "/" + DIRECTORY_NO_SLASH;
	public static final String SOUND_PATH = "audio/";
	public static final String TEXTURE_PATHS = DIRECTORY + "textures/";
	public static final String MODEL_TEXTURES_PATH = TEXTURE_PATHS + "models/";
	public static final String BLOCK_TEXTURES_PATH = TEXTURE_PATHS + "blocks/";
	public static final String ITEM_TEXTURES_PATH = TEXTURE_PATHS + "items/";
	public static final String GUI_TEXTURES_PATH = TEXTURE_PATHS + "gui/";
	public static final String LANGUAGE_PATH = DIRECTORY + "languages/";

	public static final String TEXTURE_NAME_PREFIX = "assemblyline:";

	private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US", "nl_NL", "fr_FR" };

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/AssemblyLine.cfg"));

	public static int BLOCK_ID_PREFIX = 3030;

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

	public static BlockMulti blockMulti;

	public static final int ITEM_ID_PREFIX = 13030;
	public static Item itemImprint;
	public static Item itemDisk;

	public static Logger FMLog = Logger.getLogger(AssemblyLine.MOD_NAME);

	// TODO: MAKE THIS FALSE EVERY BUILD!
	public static final boolean DEBUG = false;
	public static boolean REQUIRE_NO_POWER = false;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		FMLog.setParent(FMLLog.getLogger());
		// UniversalElectricity.register(this, 1, 2, 6, false);
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
		blockMulti = new BlockMulti(BLOCK_ID_PREFIX++);
		blockCraneController = new BlockCraneController(BLOCK_ID_PREFIX++);
		blockCraneFrame = new BlockCraneFrame(BLOCK_ID_PREFIX++);
		blockTurntable = new BlockTurntable(BLOCK_ID_PREFIX++);

		itemImprint = new ItemImprinter(CONFIGURATION.getItem("Imprint", ITEM_ID_PREFIX).getInt());
		itemDisk = new ItemDisk(CONFIGURATION.getItem("Disk", ITEM_ID_PREFIX + 1).getInt());

		REQUIRE_NO_POWER = !CONFIGURATION.get("general", "requirePower", true).getBoolean(true) || PowerSystems.runPowerLess(PowerSystems.INDUSTRIALCRAFT, PowerSystems.BUILDCRAFT, PowerSystems.MEKANISM);
		CONFIGURATION.save();

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
		GameRegistry.registerTileEntity(TileEntityMulti.class, "ALMulti");

		TabAssemblyLine.itemStack = new ItemStack(AssemblyLine.blockConveyorBelt);

		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();
		GrabDictionary.registerList();

		FMLog.info("Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " languages.");

		/* MCMOD.INFO FILE BUILDER? */
		meta.modId = AssemblyLine.MOD_ID;
		meta.name = AssemblyLine.MOD_NAME;
		meta.description = "Simi Realistic factory system for minecraft bring in conveyor belts, robotic arms, and simple factory machines";

		meta.url = "http://universalelectricity.com/assembly-line";

		meta.logoFile = "/al_logo.png";
		meta.version = AssemblyLine.VERSION;
		meta.authorList = Arrays.asList(new String[] { "DarkGuardsman" });
		meta.credits = "Please see the website.";
		meta.autogenerated = false;

		this.createStandardRecipes();
		this.createUERecipes();

	}

	private void createVanillaRecipes()
	{
		System.out.println("No crafting ingredient source found. Creating cheap-o vanilla recipes.");
		// Armbot
		GameRegistry.addRecipe(new ShapedOreRecipe(blockArmbot, new Object[] { "II ", "SIS", "MCM", 'S', "ingotIron", 'C', Item.redstoneRepeater, 'I', "ingotIron", 'M', Block.pistonBase }));
		// Disk
		GameRegistry.addRecipe(new ShapedOreRecipe(itemDisk, new Object[] { "III", "ICI", "III", 'I', itemImprint, 'C', Item.redstoneRepeater }));
		// Encoder
		GameRegistry.addRecipe(new ShapedOreRecipe(blockEncoder, new Object[] { "SIS", "SCS", "SSS", 'I', itemImprint, 'S', "ingotIron", 'C', Item.redstoneRepeater }));
		// Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockDetector, new Object[] { "SES", "SCS", "S S", 'S', "ingotIron", 'C', Block.torchRedstoneActive, 'E', Item.eyeOfEnder }));
		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', "ingotIron", 'W', Block.planks, 'M', Block.pistonBase }));
		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRejector, new Object[] { "WPW", "@R@", '@', "ingotIron", 'R', Item.redstone, 'P', Block.pistonBase, 'C', Block.torchRedstoneActive, 'W', Item.redstone }));
		// Turntable
		GameRegistry.addRecipe(new ShapedOreRecipe(blockTurntable, new Object[] { "P", "P", 'P', Block.pistonBase }));
		// Manipulator
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockManipulator, 2), new Object[] { Block.dispenser, Block.torchRedstoneActive }));
	}

	private void createUERecipes()
	{
		DarkMain.forceLoadBCItems(this, AssemblyLine.CHANNEL);
		System.out.println("BasicComponents Found...adding UE recipes for Assembly Line.");
		// Armbot
		GameRegistry.addRecipe(new ShapedOreRecipe(blockArmbot, new Object[] { "II ", "SIS", "MCM", 'S', "plateSteel", 'C', "advancedCircuit", 'I', "ingotSteel", 'M', "motor" }));
		// Disk
		GameRegistry.addRecipe(new ShapedOreRecipe(itemDisk, new Object[] { "III", "ICI", "III", 'I', itemImprint, 'C', "advancedCircuit" }));
		// Encoder
		GameRegistry.addRecipe(new ShapedOreRecipe(blockEncoder, new Object[] { "SIS", "SCS", "SSS", 'I', itemImprint, 'S', "ingotSteel", 'C', "advancedCircuit" }));
		// Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockDetector, new Object[] { "SES", "SCS", "S S", 'S', "ingotSteel", 'C', "basicCircuit", 'E', Item.eyeOfEnder }));
		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', "ingotSteel", 'W', Block.planks, 'M', "motor" }));
		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRejector, new Object[] { "CPC", "@R@", '@', "ingotSteel", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "basicCircuit" }));
		// Turntable
		GameRegistry.addRecipe(new ShapedOreRecipe(blockTurntable, new Object[] { "M", "P", 'M', "motor", 'P', Block.pistonBase }));
		// Manipulator
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockManipulator, 2), new Object[] { Block.dispenser, "basicCircuit" }));
	}

	private void createStandardRecipes()
	{
		// Imprint
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemImprint, 2), new Object[] { "R", "P", "I", 'P', Item.paper, 'R', Item.redstone, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
		// Imprinter
		GameRegistry.addRecipe(new ShapedOreRecipe(blockImprinter, new Object[] { "SIS", "SPS", "WCW", 'S', Item.ingotIron, 'C', Block.chest, 'W', Block.workbench, 'P', Block.pistonBase, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
		// Crate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 0), new Object[] { "TST", "S S", "TST", 'S', Item.ingotIron, 'T', Block.wood }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 1), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(blockCrate, 1, 0), 'S', Item.ingotIron, 'T', Block.wood }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 2), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(blockCrate, 1, 1), 'S', Item.ingotIron, 'T', Block.wood }));
	}

	public static void printSidedData(String data)
	{
		System.out.print(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? "[C]" : "[S]");
		System.out.println(" " + data);
	}
}
