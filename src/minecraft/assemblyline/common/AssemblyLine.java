package assemblyline.common;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.UETab;
import universalelectricity.prefab.UpdateNotifier;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.block.BlockArchitectTable;
import assemblyline.common.block.BlockCrate;
import assemblyline.common.block.ItemBlockCrate;
import assemblyline.common.machine.BlockMulti;
import assemblyline.common.machine.BlockMulti.MachineType;
import assemblyline.common.machine.ItemBlockMulti;
import assemblyline.common.machine.belt.BlockConveyorBelt;
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
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = AssemblyLine.CHANNEL, name = AssemblyLine.NAME, version = AssemblyLine.VERSION, dependencies = "required-after:BasicComponents")
@NetworkMod(channels = { AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class AssemblyLine
{
	@SidedProxy(clientSide = "assemblyline.client.ClientProxy", serverSide = "assemblyline.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(AssemblyLine.CHANNEL)
	public static AssemblyLine instance;

	public static final String NAME = "Assembly Line";

	public static final String VERSION = "0.1.8";

	public static final String CHANNEL = "AssemblyLine";

	public static final String RESOURCE_PATH = "/assemblyline/";
	public static final String TEXTURE_PATH = RESOURCE_PATH + "textures/";
	public static final String LANGUAGE_PATH = RESOURCE_PATH + "language/";

	private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/AssemblyLine.cfg"));

	public static final int BLOCK_ID_PREFIX = 3030;

	public static Block blockConveyorBelt;
	public static Block blockMulti;
	public static Block blockArchitectTable;
	public static Block blockCrate;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		UniversalElectricity.register(this, 1, 2, 1, false);
		instance = this;

		CONFIGURATION.load();
		blockConveyorBelt = new BlockConveyorBelt(CONFIGURATION.getBlock("Conveyor Belt", BLOCK_ID_PREFIX).getInt());
		blockMulti = new BlockMulti(CONFIGURATION.getBlock("Machine", BLOCK_ID_PREFIX + 1).getInt());
		blockArchitectTable = new BlockArchitectTable(CONFIGURATION.getBlock("Architect's Table", BLOCK_ID_PREFIX + 2).getInt());
		blockCrate = new BlockCrate(CONFIGURATION.getBlock("Crate", BLOCK_ID_PREFIX + 3).getInt());
		CONFIGURATION.save();

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		GameRegistry.registerBlock(blockConveyorBelt, "Conveyor Belt");
		GameRegistry.registerBlock(blockArchitectTable, "Architect's Table");
		GameRegistry.registerBlock(blockCrate, ItemBlockCrate.class, "Crate");
		GameRegistry.registerBlock(blockMulti, ItemBlockMulti.class, "Machine");

		UpdateNotifier.INSTANCE.checkUpdate(NAME, VERSION, "http://calclavia.com/downloads/al/recommendedversion.txt");

		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();

		int languages = 0;

		/**
		 * Load all languages.
		 */
		for (String language : LANGUAGES_SUPPORTED)
		{
			LanguageRegistry.instance().loadLocalization(LANGUAGE_PATH + language + ".properties", language, false);

			if (LanguageRegistry.instance().getStringLocalization("children", language) != "")
			{
				try
				{
					String[] children = LanguageRegistry.instance().getStringLocalization("children", language).split(",");

					for (String child : children)
					{
						if (child != "" || child != null)
						{
							LanguageRegistry.instance().loadLocalization(LANGUAGE_PATH + language + ".properties", child, false);
							languages++;
						}
					}

					languages++;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		System.out.println(NAME + ": Loaded " + languages + " languages.");

		// Add Names
		for (MachineType type : MachineType.values())
		{
			LanguageRegistry.addName(new ItemStack(blockMulti, 1, type.metadata), type.name);
		}

		// Crate
		GameRegistry.addRecipe(new ShapedOreRecipe(blockCrate, new Object[] { "SPS", "P P", "SPS", 'P', "plateSteel", 'S', Item.stick }));

		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 4), new Object[] { "III", "WMW", 'I', "ingotSteel", 'W', Block.wood, 'M', "motor" }));

		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMulti, 1, MachineType.REJECTOR.metadata), new Object[] { "WPW", "@R@", '@', "plateSteel", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "basicCircuit", 'W', "copperWire" }));

		// Manipulator
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockMulti, 1, MachineType.MANIPULATOR.metadata), new Object[] { Block.dispenser, "basicCircuit" }));

		UETab.setItemStack(new ItemStack(blockConveyorBelt));
	}
}