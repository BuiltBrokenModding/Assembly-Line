package universalelectricity.components.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.components.common.block.BlockBCOre;
import universalelectricity.components.common.block.BlockBasicMachine;
import universalelectricity.components.common.block.BlockCopperWire;
import universalelectricity.components.common.item.ItemBasic;
import universalelectricity.components.common.item.ItemBattery;
import universalelectricity.components.common.item.ItemBlockBCOre;
import universalelectricity.components.common.item.ItemBlockBasicMachine;
import universalelectricity.components.common.item.ItemBlockCopperWire;
import universalelectricity.components.common.item.ItemCircuit;
import universalelectricity.components.common.item.ItemInfiniteBattery;
import universalelectricity.components.common.item.ItemWrench;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.ConnectionHandler;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;
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

@Mod(modid = BasicComponents.CHANNEL, name = BasicComponents.NAME, version = UniversalElectricity.VERSION)
@NetworkMod(channels = BasicComponents.CHANNEL, clientSideRequired = true, serverSideRequired = false, connectionHandler = ConnectionHandler.class, packetHandler = PacketManager.class)
public class BCLoader
{
	private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US", "zh_CN", "es_ES", "it_IT", "nl_NL", "de_DE" };

	@Instance("BasicComponents")
	public static BCLoader instance;

	@SidedProxy(clientSide = "universalelectricity.components.client.ClientProxy", serverSide = "universalelectricity.components.common.CommonProxy")
	public static CommonProxy proxy;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

		/**
		 * Define the items and blocks.
		 */
		UniversalElectricity.CONFIGURATION.load();
		BasicComponents.blockBasicOre = new BlockBCOre(UniversalElectricity.CONFIGURATION.getBlock("Copper and Tin Ores", BasicComponents.BLOCK_ID_PREFIX + 0).getInt());
		BasicComponents.blockCopperWire = new BlockCopperWire(UniversalElectricity.CONFIGURATION.getBlock("Copper_Wire", BasicComponents.BLOCK_ID_PREFIX + 1).getInt());
		BasicComponents.blockMachine = new BlockBasicMachine(UniversalElectricity.CONFIGURATION.getBlock("Basic Machine", BasicComponents.BLOCK_ID_PREFIX + 4).getInt(), 0);

		BasicComponents.itemInfiniteBattery = new ItemInfiniteBattery(UniversalElectricity.CONFIGURATION.getItem("Infinite Battery", BasicComponents.ITEM_ID_PREFIX + 0).getInt());
		BasicComponents.itemBattery = new ItemBattery(UniversalElectricity.CONFIGURATION.getItem("Battery", BasicComponents.ITEM_ID_PREFIX + 1).getInt());
		BasicComponents.itemWrench = new ItemWrench(UniversalElectricity.CONFIGURATION.getItem("Universal Wrench", BasicComponents.ITEM_ID_PREFIX + 2).getInt(), 20);
		BasicComponents.itemCircuit = new ItemCircuit(UniversalElectricity.CONFIGURATION.getItem("Circuit", BasicComponents.ITEM_ID_PREFIX + 3).getInt(), 16);

		BasicComponents.itemBronzeDust = new ItemBasic("dustBronze", UniversalElectricity.CONFIGURATION.getItem("Bronze Dust", BasicComponents.ITEM_ID_PREFIX + 8).getInt());
		BasicComponents.itemSteelDust = new ItemBasic("dustSteel", UniversalElectricity.CONFIGURATION.getItem("Steel Dust", BasicComponents.ITEM_ID_PREFIX + 9).getInt());

		BasicComponents.itemMotor = new ItemBasic("motor", UniversalElectricity.CONFIGURATION.getItem("Motor", BasicComponents.ITEM_ID_PREFIX + 14).getInt());

		// Register Blocks
		GameRegistry.registerBlock(BasicComponents.blockBasicOre, ItemBlockBCOre.class, "Ore");
		GameRegistry.registerBlock(BasicComponents.blockMachine, ItemBlockBasicMachine.class, "Basic Machine");
		GameRegistry.registerBlock(BasicComponents.blockCopperWire, ItemBlockCopperWire.class, "Copper Wire");

		BasicComponents.copperOreGeneration = new OreGenReplaceStone("Copper Ore", "oreCopper", new ItemStack(BasicComponents.blockBasicOre, 1, 0), 60, 26, 4).enable(UniversalElectricity.CONFIGURATION);
		BasicComponents.tinOreGeneration = new OreGenReplaceStone("Tin Ore", "oreTin", new ItemStack(BasicComponents.blockBasicOre, 1, 1), 60, 23, 4).enable(UniversalElectricity.CONFIGURATION);

		UniversalElectricity.CONFIGURATION.save();

		/**
		 * Registering all Basic Component items into the Forge Ore Dictionary.
		 */
		OreDictionary.registerOre("copperWire", BasicComponents.blockCopperWire);

		OreDictionary.registerOre("coalGenerator", ((BlockBasicMachine) BasicComponents.blockMachine).getCoalGenerator());
		OreDictionary.registerOre("batteryBox", ((BlockBasicMachine) BasicComponents.blockMachine).getBatteryBox());
		OreDictionary.registerOre("electricFurnace", ((BlockBasicMachine) BasicComponents.blockMachine).getElectricFurnace());

		OreDictionary.registerOre("battery", BasicComponents.itemBattery);
		OreDictionary.registerOre("wrench", BasicComponents.itemWrench);
		OreDictionary.registerOre("motor", BasicComponents.itemMotor);

		OreDictionary.registerOre("basicCircuit", new ItemStack(BasicComponents.itemCircuit, 1, 0));
		OreDictionary.registerOre("advancedCircuit", new ItemStack(BasicComponents.itemCircuit, 1, 1));
		OreDictionary.registerOre("eliteCircuit", new ItemStack(BasicComponents.itemCircuit, 1, 2));

		OreDictionary.registerOre("dustBronze", BasicComponents.itemBronzeDust);
		OreDictionary.registerOre("dustSteel", BasicComponents.itemSteelDust);

		BasicComponents.requestIngots(0);
		BasicComponents.requestPlates(0);

		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();

		System.out.println("Basic Components Loaded: " + TranslationHelper.loadLanguages(BasicComponents.LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");

		OreGenerator.addOre(BasicComponents.copperOreGeneration);
		OreGenerator.addOre(BasicComponents.tinOreGeneration);

		// Recipe Registry
		// Motor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemMotor), new Object[] { "@!@", "!#!", "@!@", '!', "ingotSteel", '#', Item.ingotIron, '@', "copperWire" }));
		// Wrench
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemWrench), new Object[] { " S ", " DS", "S  ", 'S', "ingotSteel", 'D', Item.diamond }));
		// Battery Box
		GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("batteryBox").get(0), new Object[] { "SSS", "BBB", "SSS", 'B', ElectricItemHelper.getUncharged(BasicComponents.itemBattery), 'S', "ingotSteel" }));
		// Coal Generator
		GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("coalGenerator").get(0), new Object[] { "MMM", "MOM", "MCM", 'M', "ingotSteel", 'C', BasicComponents.itemMotor, 'O', Block.furnaceIdle }));
		GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("coalGenerator").get(0), new Object[] { "MMM", "MOM", "MCM", 'M', "ingotBronze", 'C', BasicComponents.itemMotor, 'O', Block.furnaceIdle }));
		// Electric Furnace
		GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("electricFurnace").get(0), new Object[] { "SSS", "SCS", "SMS", 'S', "ingotSteel", 'C', BasicComponents.itemCircuit, 'M', BasicComponents.itemMotor }));
		// Copper
		FurnaceRecipes.smelting().addSmelting(BasicComponents.blockBasicOre.blockID, 0, OreDictionary.getOres("ingotCopper").get(0), 0.7f);
		// Copper Wire
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.blockCopperWire, 6), new Object[] { "!!!", "@@@", "!!!", '!', Block.cloth, '@', "ingotCopper" }));
		// Tin
		FurnaceRecipes.smelting().addSmelting(BasicComponents.blockBasicOre.blockID, 1, OreDictionary.getOres("ingotTin").get(0), 0.7f);
		// Battery
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemBattery), new Object[] { " T ", "TRT", "TCT", 'T', "ingotTin", 'R', Item.redstone, 'C', Item.coal }));
		// Steel
		RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemSteelDust), new Object[] { " C ", "CIC", " C ", 'C', new ItemStack(Item.coal, 1, 1), 'I', Item.ingotIron }), "Steel Dust", UniversalElectricity.CONFIGURATION, true);
		RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemSteelDust), new Object[] { " C ", "CIC", " C ", 'C', new ItemStack(Item.coal, 1, 0), 'I', Item.ingotIron }), "Steel Dust", UniversalElectricity.CONFIGURATION, true);
		GameRegistry.addSmelting(BasicComponents.itemSteelDust.itemID, OreDictionary.getOres("ingotSteel").get(0), 0.8f);
		// Bronze
		RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemBronzeDust), new Object[] { "!#!", '!', "ingotCopper", '#', "ingotTin" }), "Bronze Dust", UniversalElectricity.CONFIGURATION, true);
		GameRegistry.addSmelting(BasicComponents.itemBronzeDust.itemID, OreDictionary.getOres("ingotBronze").get(0), 0.6f);

		// Circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemCircuit, 1, 0), new Object[] { "!#!", "#@#", "!#!", '@', "plateBronze", '#', Item.redstone, '!', "copperWire" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemCircuit, 1, 0), new Object[] { "!#!", "#@#", "!#!", '@', "plateSteel", '#', Item.redstone, '!', "copperWire" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemCircuit, 1, 1), new Object[] { "@@@", "#?#", "@@@", '@', Item.redstone, '?', Item.diamond, '#', BasicComponents.itemCircuit }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemCircuit, 1, 2), new Object[] { "@@@", "?#?", "@@@", '@', Item.ingotGold, '?', new ItemStack(BasicComponents.itemCircuit, 1, 1), '#', Block.blockLapis }));
	}
}
