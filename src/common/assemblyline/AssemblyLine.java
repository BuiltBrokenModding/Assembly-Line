package assemblyline;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.belts.BlockConveyorBelt;
import assemblyline.belts.TileEntityConveyorBelt;
import assemblyline.interaction.BlockInteraction;
import assemblyline.interaction.ItemMachine;
import assemblyline.interaction.TileEntityEjector;
import assemblyline.interaction.TileEntityMachineInput;
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
	
	public static final Configuration config = new Configuration(new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), "UniversalElectricity/ConveyorBelts.cfg"));
	public static int machineID = configurationProperties();
	public static int machine2ID;
	public static int beltBlockID;
	public static boolean animationOn;
	public static final String TEXTURE_PATH = "/textures";
	public static final Block blockConveyorBelt = new BlockConveyorBelt(beltBlockID);
	public static final Block blockMachine = new BlockInteraction(machineID);

	public static int configurationProperties()
	{
		config.load();
		beltBlockID = Integer.parseInt(config.getBlock(Configuration.CATEGORY_BLOCK, "BeltBlockID", 3003).value);
		machineID = Integer.parseInt(config.getBlock(Configuration.CATEGORY_BLOCK, "MachineID", 3005).value);
		animationOn = Boolean.parseBoolean(config.get(Configuration.CATEGORY_GENERAL, "BeltAnimationOn", true).value);
		config.save();
		return machineID;
	}

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		instance = this;
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		GameRegistry.registerBlock(blockConveyorBelt);
		GameRegistry.registerBlock(blockMachine, ItemMachine.class);
		proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		GameRegistry.registerTileEntity(TileEntityConveyorBelt.class, "belt");
		GameRegistry.registerTileEntity(TileEntityEjector.class, "ejector");
		GameRegistry.registerTileEntity(TileEntityMachineInput.class, "scop");
		proxy.init();
		// Names
		LanguageRegistry.addName(new ItemStack(blockConveyorBelt, 1), "Coneveyor Belt");
		LanguageRegistry.addName(new ItemStack(blockMachine, 1, 0), "Ejector");
		LanguageRegistry.addName(new ItemStack(blockMachine, 1, 4), "MachineInput");
		LanguageRegistry.addName(new ItemStack(blockMachine, 1, 8), "FutureBlock");
		LanguageRegistry.addName(new ItemStack(blockMachine, 1, 12), "FutureBlock");
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt), new Object[]
		{ "III", "MCM", 'I', Item.ingotIron, 'M', "motor", 'C', "basicCircuit" }));

		// Rejector
		GameRegistry.addRecipe(new ItemStack(blockMachine, 1, 0), new Object[]
		{ "WPW", "@R@", '@', "plateSteel", 'R', Item.redstone, 'P', Block.pistonBase, 'C', "basicCircuit", 'W', "copperWire" });

		// Retriever
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockMachine, 1, 4), new Object[]
		{ Block.dispenser, "basicCircuit" }));
		proxy.postInit();
	}

}