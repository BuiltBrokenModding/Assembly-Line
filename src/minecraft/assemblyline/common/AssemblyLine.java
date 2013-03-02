package assemblyline.common;

import ic2.api.Ic2Recipes;

import java.io.File;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.UpdateNotifier;
import universalelectricity.prefab.multiblock.BlockMulti;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.block.BlockCrate;
import assemblyline.common.block.BlockTurntable;
import assemblyline.common.block.ItemBlockCrate;
import assemblyline.common.machine.BlockManipulator;
import assemblyline.common.machine.BlockRejector;
import assemblyline.common.machine.armbot.BlockArmbot;
import assemblyline.common.machine.belt.BlockConveyorBelt;
import assemblyline.common.machine.command.GrabDictionary;
import assemblyline.common.machine.crane.BlockCraneController;
import assemblyline.common.machine.crane.BlockCraneFrame;
import assemblyline.common.machine.detector.BlockDetector;
import assemblyline.common.machine.encoder.BlockEncoder;
import assemblyline.common.machine.encoder.ItemDisk;
import assemblyline.common.machine.imprinter.BlockImprinter;
import assemblyline.common.machine.imprinter.ItemImprinter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = AssemblyLine.CHANNEL, name = AssemblyLine.NAME, version = AssemblyLine.VERSION, dependencies = "after:BasicComponents")
@NetworkMod(channels = { AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class AssemblyLine
{
	@SidedProxy(clientSide = "assemblyline.client.ClientProxy", serverSide = "assemblyline.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(AssemblyLine.CHANNEL)
	public static AssemblyLine instance;

	public static final String NAME = "Assembly Line";

	public static final String VERSION = "0.2.7";

	public static final String CHANNEL = "AssemblyLine";

	public static final String DIRECTORY_NO_SLASH = "assemblyline/";
	public static final String DIRECTORY = "/" + DIRECTORY_NO_SLASH;
	public static final String TEXTURE_PATH = DIRECTORY + "textures/";
	public static final String LANGUAGE_PATH = DIRECTORY + "language/";
	public static final String BLOCK_TEXTURE_PATH = TEXTURE_PATH + "blocks.png";
	public static final String ITEM_TEXTURE_PATH = TEXTURE_PATH + "items.png";

	private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/AssemblyLine.cfg"));

	public static final int BLOCK_ID_PREFIX = 3030;

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

	public static Logger FMLog = Logger.getLogger(AssemblyLine.NAME);

	//TODO: MAKE THIS FALSE EVERY BUILD!
	public static final boolean DEBUG = false;
	public static boolean REQUIRE_NO_POWER = false;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		FMLog.setParent(FMLLog.getLogger());
		UniversalElectricity.register(this, 1, 2, 6, false);
		instance = this;

		CONFIGURATION.load();
		blockConveyorBelt = new BlockConveyorBelt(CONFIGURATION.getBlock("Conveyor Belt", BLOCK_ID_PREFIX).getInt());
		blockManipulator = new BlockManipulator(CONFIGURATION.getBlock("Manipulator", BLOCK_ID_PREFIX + 1).getInt());
		blockCrate = new BlockCrate(CONFIGURATION.getBlock("Crate", BLOCK_ID_PREFIX + 3).getInt(), 0);
		blockImprinter = new BlockImprinter(CONFIGURATION.getBlock("Imprinter", BLOCK_ID_PREFIX + 4).getInt(), 4);
		blockDetector = new BlockDetector(CONFIGURATION.getBlock("Detector", BLOCK_ID_PREFIX + 5).getInt(), 1);
		blockRejector = new BlockRejector(CONFIGURATION.getBlock("Rejector", BLOCK_ID_PREFIX + 6).getInt());
		blockEncoder = new BlockEncoder(CONFIGURATION.getBlock("Encoder", BLOCK_ID_PREFIX + 7).getInt(), 7);
		blockArmbot = new BlockArmbot(CONFIGURATION.getBlock("Armbot", BLOCK_ID_PREFIX + 8).getInt());
		blockMulti = new BlockMulti(CONFIGURATION.getBlock("Multiblock", BLOCK_ID_PREFIX + 9).getInt());
		blockCraneController = new BlockCraneController(CONFIGURATION.getBlock("Crane Controller", BLOCK_ID_PREFIX + 10).getInt());
		blockCraneFrame = new BlockCraneFrame(CONFIGURATION.getBlock("Crane Frame", BLOCK_ID_PREFIX + 11).getInt());
		blockTurntable = new BlockTurntable(CONFIGURATION.getBlock("Turntable", BLOCK_ID_PREFIX + 12).getInt(), 10);

		itemImprint = new ItemImprinter(CONFIGURATION.getItem("Imprint", ITEM_ID_PREFIX).getInt());
		itemDisk = new ItemDisk(CONFIGURATION.getItem("Disk", ITEM_ID_PREFIX + 1).getInt());
		
		REQUIRE_NO_POWER = DEBUG || !CONFIGURATION.get("general", "requirePower", true).getBoolean(true);
		CONFIGURATION.save();

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		GameRegistry.registerBlock(blockConveyorBelt, "Conveyor Belt");
		GameRegistry.registerBlock(blockCrate, ItemBlockCrate.class, "Crate");
		GameRegistry.registerBlock(blockManipulator, "Manipulator");
		GameRegistry.registerBlock(blockImprinter, "Imprinter");
		GameRegistry.registerBlock(blockEncoder, "Encoder");
		GameRegistry.registerBlock(blockDetector, "Detector");
		GameRegistry.registerBlock(blockRejector, "Rejector");
		GameRegistry.registerBlock(blockArmbot, "Armbot");
		GameRegistry.registerBlock(blockTurntable, "Turntable");
		GameRegistry.registerBlock(blockCraneController, "Crane Controller");
		GameRegistry.registerBlock(blockCraneFrame, "Crane Frame");

		TabAssemblyLine.itemStack = new ItemStack(AssemblyLine.blockConveyorBelt);
		UpdateNotifier.INSTANCE.checkUpdate(NAME, VERSION, "http://calclavia.com/downloads/al/recommendedversion.txt");

		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();
		GrabDictionary.registerList();

		FMLog.info("Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " languages.");

		// Crane Controller
		// GameRegistry.addRecipe(new ShapedOreRecipe(blockCraneController, new Object[] { "SFS",
		// "MCM", "SMS", 'F', blockCraneFrame, 'S', "plateSteel", 'C', "advancedCircuit", 'I',
		// "ingotSteel", 'M', "motor" }));

		// Crane Frame
		// GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCraneFrame, 4), new
		// Object[] { "ISI", "ISI", 'I', Item.ingotIron, 'S', Item.stick }));
		// GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCraneFrame, 4), new
		// Object[] { "ISI", "ISI", 'I', "ingotBronze", 'S', Item.stick }));

		boolean ic2 = Loader.isModLoaded("IC2");
		boolean ue = Loader.isModLoaded("BasicComponents");

		createStandardRecipes();
		if (ic2)
		{
			createIC2Recipes();
		}
		if (ue)
		{
			createUERecipes();
		}
		if (!ic2 && !ue)
		{
			createVanillaRecipes();
			REQUIRE_NO_POWER = true;
		}
	}

	private void createVanillaRecipes()
	{
		// Armbot
		GameRegistry.addRecipe(new ShapedOreRecipe(blockArmbot, new Object[] { "II ", "SIS", "MCM", 'S', "ingotIron", 'C', Item.redstoneRepeater, 'I', "ingotIron", 'M', Block.pistonBase }));
		// Disk
		GameRegistry.addRecipe(new ShapedOreRecipe(itemDisk, new Object[] { "III", "ICI", "III", 'I', itemImprint, 'C', Item.redstoneRepeater }));
		// Encoder
		GameRegistry.addRecipe(new ShapedOreRecipe(blockEncoder, new Object[] { "SIS", "SCS", "SSS", 'I', itemImprint, 'S', "ingotIron", 'C', Item.redstoneRepeater }));
		// Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockDetector, new Object[] { "SES", "SCS", "SPS", 'S', "ingotIron", 'C', Block.torchRedstoneActive, 'E', Item.eyeOfEnder }));
		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', "ingotIron", 'W', Block.wood, 'M', Block.pistonBase }));
		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRejector, new Object[] { "WPW", "@R@", '@', "ingotIron", 'R', Item.redstone, 'P', Block.pistonBase, 'C', Block.torchRedstoneActive, 'W', Item.redstone }));
		// Turntable
		GameRegistry.addRecipe(new ShapedOreRecipe(blockTurntable, new Object[] { "P", "P", 'P', Block.pistonBase }));
		// Manipulator
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockManipulator, 2), new Object[] { Block.dispenser, Block.torchRedstoneActive }));
	}

	private void createUERecipes()
	{
		// Armbot
		GameRegistry.addRecipe(new ShapedOreRecipe(blockArmbot, new Object[] { "II ", "SIS", "MCM", 'S', "plateSteel", 'C', "advancedCircuit", 'I', "ingotSteel", 'M', "motor" }));
		// Disk
		GameRegistry.addRecipe(new ShapedOreRecipe(itemDisk, new Object[] { "III", "ICI", "III", 'I', itemImprint, 'C', "advancedCircuit" }));
		// Encoder
		GameRegistry.addRecipe(new ShapedOreRecipe(blockEncoder, new Object[] { "SIS", "SCS", "SSS", 'I', itemImprint, 'S', "ingotSteel", 'C', "advancedCircuit" }));
		// Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockDetector, new Object[] { "SES", "SCS", "SPS", 'S', "ingotSteel", 'C', "basicCircuit", 'E', Item.eyeOfEnder }));
		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', "ingotSteel", 'W', Block.wood, 'M', "motor" }));
		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRejector, new Object[] { "WPW", "@R@", '@', "ingotSteel", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "basicCircuit", 'W', "copperWire" }));
		// Turntable
		GameRegistry.addRecipe(new ShapedOreRecipe(blockTurntable, new Object[] { "M", "P", 'M', "motor", 'P', Block.pistonBase }));
		// Manipulator
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockManipulator, 2), new Object[] { Block.dispenser, "basicCircuit" }));
	}

	private void createIC2Recipes()
	{
		// Armbot
		Ic2Recipes.addCraftingRecipe(new ItemStack(blockArmbot, 1), new Object[] { "II ", "SIS", "MCM", 'S', "advancedAlloy", 'C', "electronicCircuit", 'I', "ingotRefinedIron", 'M', "generator" });
		// Disk
		Ic2Recipes.addCraftingRecipe(new ItemStack(itemDisk, 1), new Object[] { "III", "ICI", "III", 'I', itemImprint, 'C', "advancedCircuit" });
		// Encoder
		Ic2Recipes.addCraftingRecipe(new ItemStack(blockEncoder, 1), new Object[] { "SIS", "SCS", "SSS", 'I', itemImprint, 'S', "ingotRefinedIron", 'C', "advancedCircuit" });
		// Detector
		Ic2Recipes.addCraftingRecipe(new ItemStack(blockDetector, 1), new Object[] { "SES", "SCS", "SPS", 'S', "ingotRefinedIron", 'C', "electronicCircuit", 'E', Item.eyeOfEnder });
		// Conveyor Belt
		Ic2Recipes.addCraftingRecipe(new ItemStack(blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', "ingotRefinedIron", 'W', Block.wood, 'M', "generator" });
		// Rejector
		Ic2Recipes.addCraftingRecipe(new ItemStack(blockRejector, 1), new Object[] { "WPW", "@R@", '@', "ingotRefinedIron", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "electronicCircuit", 'W', "insulatedCopperCableItem" });
		// Turntable
		Ic2Recipes.addCraftingRecipe(new ItemStack(blockTurntable, 1), new Object[] { "M", "P", 'M', "generator", 'P', Block.pistonBase });
		// Manipulator
		Ic2Recipes.addCraftingRecipe(new ItemStack(blockManipulator, 2), new Object[] { Block.dispenser, "electronicCircuit" });
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
