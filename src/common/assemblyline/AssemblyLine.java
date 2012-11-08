package assemblyline;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UEConfig;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.belts.BlockConveyorBelt;
import assemblyline.belts.TileEntityConveyorBelt;
import assemblyline.machines.BlockMulti;
import assemblyline.machines.BlockMulti.MachineType;
import assemblyline.machines.ItemBlockMulti;
import assemblyline.machines.TileEntityManipulator;
import assemblyline.machines.TileEntityRejector;
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

@Mod(modid = "AssemblyLine", name = "Assembly Line", version = AssemblyLine.VERSION, dependencies = "after:BasicComponents")
@NetworkMod(channels =
{ AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class AssemblyLine
{
	@SidedProxy(clientSide = "assemblyline.ALClientProxy", serverSide = "assemblyline.ALCommonProxy")
	public static ALCommonProxy proxy;

	@Instance("AssemblyLine")
	public static AssemblyLine instance;

	public static final String VERSION = "0.1.0";

	public static final String CHANNEL = "AssemblyLine";

	public static final String TEXTURE_PATH = "/assemblyline/textures/";

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/AssemblyLine.cfg"));

	public static final int BLOCK_ID_PREFIX = 3003;
	public static final Block blockConveyorBelt = new BlockConveyorBelt(UEConfig.getBlockConfigID(CONFIGURATION, "Conveyor Belt", BLOCK_ID_PREFIX));
	public static final Block blockInteraction = new BlockMulti(UEConfig.getBlockConfigID(CONFIGURATION, "Machine", BLOCK_ID_PREFIX + 1));

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		instance = this;
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		GameRegistry.registerBlock(blockConveyorBelt);
		GameRegistry.registerBlock(blockInteraction, ItemBlockMulti.class);
		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		proxy.init();

		GameRegistry.registerTileEntity(TileEntityConveyorBelt.class, "ConveyorBelt");
		GameRegistry.registerTileEntity(TileEntityRejector.class, "Sorter");
		GameRegistry.registerTileEntity(TileEntityManipulator.class, "Manipulator");

		// Add Names
		LanguageRegistry.addName(new ItemStack(blockConveyorBelt, 1), "Conveyor Belt");

		for (MachineType type : MachineType.values())
		{
			LanguageRegistry.addName(new ItemStack(blockInteraction, 1, type.metadata), type.name);
		}

		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 2), new Object[]
		{ "III", "WMW", 'I', "ingotSteel", 'W', Block.wood, 'M', "motor" }));

		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockInteraction, 1, MachineType.SORTER.metadata), new Object[]
		{ "WPW", "@R@", '@', "plateSteel", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "basicCircuit", 'W', "copperWire" }));

		// Retriever
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockInteraction, 1, MachineType.MANIPULATOR.metadata), new Object[]
		{ Block.dispenser, "basicCircuit" }));
	}
}