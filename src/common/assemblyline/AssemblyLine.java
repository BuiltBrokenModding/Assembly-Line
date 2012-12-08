package assemblyline;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.UETab;
import universalelectricity.prefab.UpdateNotifier;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.machine.BlockArchitectTable;
import assemblyline.machine.BlockMulti;
import assemblyline.machine.BlockMulti.MachineType;
import assemblyline.machine.ItemBlockMulti;
import assemblyline.machine.TileEntityManipulator;
import assemblyline.machine.TileEntityRejector;
import assemblyline.machine.belt.BlockConveyorBelt;
import assemblyline.machine.belt.TileEntityConveyorBelt;
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

@Mod(modid = AssemblyLine.CHANNEL, name = AssemblyLine.NAME, version = AssemblyLine.VERSION, dependencies = "after:BasicComponents")
@NetworkMod(channels = { AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class AssemblyLine
{
	@SidedProxy(clientSide = "assemblyline.ALClientProxy", serverSide = "assemblyline.ALCommonProxy")
	public static ALCommonProxy proxy;

	@Instance(AssemblyLine.CHANNEL)
	public static AssemblyLine instance;

	public static final String NAME = "Assembly Line";

	public static final String VERSION = "0.1.4";

	public static final String CHANNEL = "AssemblyLine";

	public static final String TEXTURE_PATH = "/assemblyline/textures/";

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/AssemblyLine.cfg"));

	public static final int BLOCK_ID_PREFIX = 3030;
	public static Block blockConveyorBelt;
	public static Block blockInteraction;
	public static Block blockArchitectTable;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		UniversalElectricity.register(this, 1, 1, 3, false);
		instance = this;

		CONFIGURATION.load();
		blockConveyorBelt = new BlockConveyorBelt(CONFIGURATION.getBlock("Conveyor Belt", BLOCK_ID_PREFIX).getInt());
		blockInteraction = new BlockMulti(CONFIGURATION.getBlock("Machine", BLOCK_ID_PREFIX + 1).getInt());
		blockArchitectTable = new BlockArchitectTable(CONFIGURATION.getBlock("Architect's Table", BLOCK_ID_PREFIX + 2).getInt());
		CONFIGURATION.save();

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		GameRegistry.registerBlock(blockConveyorBelt);
		GameRegistry.registerBlock(blockArchitectTable);
		GameRegistry.registerBlock(blockInteraction, ItemBlockMulti.class);

		UpdateNotifier.INSTANCE.checkUpdate(NAME, VERSION, "http://calclavia.com/downloads/al/modversion.txt");

		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();

		//TODO Load language files and use those for block naming
		
		GameRegistry.registerTileEntity(TileEntityConveyorBelt.class, "ConveyorBelt");
		GameRegistry.registerTileEntity(TileEntityRejector.class, "Sorter");
		GameRegistry.registerTileEntity(TileEntityManipulator.class, "Manipulator");

		// Add Names
		LanguageRegistry.addName(blockConveyorBelt, "Conveyor Belt");
		LanguageRegistry.addName(blockArchitectTable, "Architect's Table");

		for (MachineType type : MachineType.values())
		{
			LanguageRegistry.addName(new ItemStack(blockInteraction, 1, type.metadata), type.name);
		}

		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 2), new Object[] { "III", "WMW", 'I', "ingotSteel", 'W', Block.wood, 'M', "motor" }));

		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockInteraction, 1, MachineType.SORTER.metadata), new Object[] { "WPW", "@R@", '@', "plateSteel", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "basicCircuit", 'W', "copperWire" }));

		// Retriever
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockInteraction, 1, MachineType.MANIPULATOR.metadata), new Object[] { Block.dispenser, "basicCircuit" }));

		UETab.setItemStack(new ItemStack(blockConveyorBelt));
	}
}