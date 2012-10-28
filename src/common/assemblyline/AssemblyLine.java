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
import assemblyline.interaction.BlockInteraction;
import assemblyline.interaction.BlockInteraction.InteractMachineMetadata;
import assemblyline.interaction.ItemBlockInteraction;
import assemblyline.interaction.TileEntityEjector;
import assemblyline.interaction.TileEntityInjector;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "AssemblyLine", name = "Assembly Line", version = AssemblyLine.VERSION, dependencies = "after:BasicComponents")
@NetworkMod(channels = { AssemblyLine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
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
	public static final Block blockInteraction = new BlockInteraction(UEConfig.getBlockConfigID(CONFIGURATION, "Machine", BLOCK_ID_PREFIX+1));

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		instance = this;
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		GameRegistry.registerBlock(blockConveyorBelt);
		GameRegistry.registerBlock(blockInteraction, ItemBlockInteraction.class);
		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		GameRegistry.registerTileEntity(TileEntityConveyorBelt.class, "belt");
		GameRegistry.registerTileEntity(TileEntityEjector.class, "ejector");
		GameRegistry.registerTileEntity(TileEntityInjector.class, "scop");
		proxy.init();
		// Names
		LanguageRegistry.addName(new ItemStack(blockConveyorBelt, 1), "Coneveyor Belt");
		LanguageRegistry.addName(new ItemStack(blockInteraction, 1, InteractMachineMetadata.EJECTOR.metadata), InteractMachineMetadata.EJECTOR.name);
		LanguageRegistry.addName(new ItemStack(blockInteraction, 1, InteractMachineMetadata.INJECTOR.metadata), InteractMachineMetadata.EJECTOR.name);
		LanguageRegistry.addName(new ItemStack(blockInteraction, 1, 8), "FutureBlock");
		LanguageRegistry.addName(new ItemStack(blockInteraction, 1, 12), "FutureBlock");
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt), new Object[]
		{ "III", "MCM", 'I', Item.ingotIron, 'M', "motor", 'C', "basicCircuit" }));

		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockInteraction, 1, 0), new Object[]
		{ "WPW", "@R@", '@', "plateSteel", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "basicCircuit", 'W', "copperWire" }));

		// Retriever
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockInteraction, 1, 4), new Object[]
		{ Block.dispenser, "basicCircuit" }));
		proxy.postInit();
	}

}