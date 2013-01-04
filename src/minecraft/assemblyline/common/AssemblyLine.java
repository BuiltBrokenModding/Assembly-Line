package assemblyline.common;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.UETab;
import universalelectricity.prefab.UpdateNotifier;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.block.BlockCrate;
import assemblyline.common.block.ItemBlockCrate;
import assemblyline.common.machine.BlockManipulator;
import assemblyline.common.machine.BlockRejector;
import assemblyline.common.machine.belt.BlockConveyorBelt;
import assemblyline.common.machine.detector.BlockDetector;
import assemblyline.common.machine.encoder.BlockEncoder;
import assemblyline.common.machine.encoder.ItemDisk;
import assemblyline.common.machine.imprinter.BlockImprinter;
import assemblyline.common.machine.imprinter.ItemImprinter;
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

@Mod(modid = AssemblyLine.CHANNEL, name = AssemblyLine.NAME, version = AssemblyLine.VERSION, dependencies = "required-after:BasicComponents")
@NetworkMod(channels = { AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class AssemblyLine
{
	@SidedProxy(clientSide = "assemblyline.client.ClientProxy", serverSide = "assemblyline.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(AssemblyLine.CHANNEL)
	public static AssemblyLine instance;

	public static final String NAME = "Assembly Line";

	public static final String VERSION = "0.2.1";

	public static final String CHANNEL = "AssemblyLine";

	public static final String RESOURCE_PATH = "/assemblyline/";
	public static final String TEXTURE_PATH = RESOURCE_PATH + "textures/";
	public static final String LANGUAGE_PATH = RESOURCE_PATH + "language/";
	public static final String BLOCK_TEXTURE_PATH = TEXTURE_PATH + "blocks.png";
	public static final String ITEM_TEXTURE_PATH = TEXTURE_PATH + "items.png";

	private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/AssemblyLine.cfg"));

	public static final int BLOCK_ID_PREFIX = 3030;

	public static Block blockConveyorBelt;
	public static Block blockManipulator;
	public static Block blockCrate;
	public static Block blockImprinter;
	public static Block blockEncoder;
	public static Block blockDetector;
	public static Block blockRejector;

	public static final int ITEM_ID_PREFIX = 3030;
	public static Item itemImprint;
	public static Item itemDisk;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		UniversalElectricity.register(this, 1, 2, 1, false);
		instance = this;

		CONFIGURATION.load();
		blockConveyorBelt = new BlockConveyorBelt(CONFIGURATION.getBlock("Conveyor Belt", BLOCK_ID_PREFIX).getInt());
		blockManipulator = new BlockManipulator(CONFIGURATION.getBlock("Manipulator", BLOCK_ID_PREFIX + 1).getInt());
		// blockEncoder = new BlockEncoder2(CONFIGURATION.getBlock("Encoder", BLOCK_ID_PREFIX +
		// 2).getInt());
		blockCrate = new BlockCrate(CONFIGURATION.getBlock("Crate", BLOCK_ID_PREFIX + 3).getInt(), 0);
		blockImprinter = new BlockImprinter(CONFIGURATION.getBlock("Imprinter", BLOCK_ID_PREFIX + 4).getInt(), 0);
		blockDetector = new BlockDetector(CONFIGURATION.getBlock("Detector", BLOCK_ID_PREFIX + 5).getInt(), 1);
		blockRejector = new BlockRejector(CONFIGURATION.getBlock("Rejector", BLOCK_ID_PREFIX + 6).getInt());
		blockEncoder = new BlockEncoder(CONFIGURATION.getBlock("Programmer", BLOCK_ID_PREFIX + 7).getInt(), 0);

		itemImprint = new ItemImprinter(CONFIGURATION.getBlock("Imprint", ITEM_ID_PREFIX).getInt());
		itemDisk = new ItemDisk(CONFIGURATION.getBlock("Disk", ITEM_ID_PREFIX + 1).getInt());
		CONFIGURATION.save();

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		GameRegistry.registerBlock(blockConveyorBelt, "Conveyor Belt");
		GameRegistry.registerBlock(blockCrate, ItemBlockCrate.class, "Crate");
		GameRegistry.registerBlock(blockManipulator, "Manipulator");
		GameRegistry.registerBlock(blockImprinter, "Imprinter");
		GameRegistry.registerBlock(blockEncoder, "Encoder");
		GameRegistry.registerBlock(blockDetector, "Detector");
		GameRegistry.registerBlock(blockRejector, "Rejector");

		UpdateNotifier.INSTANCE.checkUpdate(NAME, VERSION, "http://calclavia.com/downloads/al/recommendedversion.txt");

		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();

		System.out.println(NAME + " Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " languages.");

		// Disk
		GameRegistry.addRecipe(new ShapedOreRecipe(itemDisk, new Object[] { "R", "P", "I", 'P', Item.paper, 'R', Item.redstone, 'I', new ItemStack(Item.dyePowder, 1, 0) }));

		// Encoder
		GameRegistry.addRecipe(new ShapedOreRecipe(blockEncoder, new Object[] { "S S", "SCS", "SSS", 'S', "ingotSteel", 'C', "eliteCircuit" }));

		// Imprint
		GameRegistry.addRecipe(new ShapedOreRecipe(itemImprint, new Object[] { "R", "P", "I", 'P', Item.paper, 'R', Item.redstone, 'I', new ItemStack(Item.dyePowder, 1, 0) }));

		// Imprinter
		GameRegistry.addRecipe(new ShapedOreRecipe(blockImprinter, new Object[] { "SIS", "SPS", "WWW", 'S', "ingotSteel", 'W', Block.wood, 'P', Block.pistonStickyBase, 'I', new ItemStack(Item.dyePowder, 1, 0) }));

		// Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockDetector, new Object[] { "SES", "SCS", "SPS", 'S', "ingotSteel", 'C', "basicCircuit", 'E', Item.eyeOfEnder }));

		// Crate
		GameRegistry.addRecipe(new ShapedOreRecipe(blockCrate, new Object[] { "TST", "S S", "TST", 'S', "ingotSteel", 'T', Item.stick }));

		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 4), new Object[] { "III", "WMW", 'I', "ingotSteel", 'W', Block.wood, 'M', "motor" }));

		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRejector, new Object[] { "WPW", "@R@", '@', "plateSteel", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "basicCircuit", 'W', "copperWire" }));

		// Manipulator
		GameRegistry.addRecipe(new ShapelessOreRecipe(blockManipulator, new Object[] { Block.dispenser, "basicCircuit" }));

		UETab.setItemStack(new ItemStack(blockConveyorBelt));
	}
}