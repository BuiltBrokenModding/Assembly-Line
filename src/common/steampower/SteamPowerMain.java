package steampower;
import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import steampower.turbine.BlockGenerator;
import steampower.turbine.BlockSteamPiston;
import steampower.turbine.ItemEngine;
import steampower.turbine.TileEntitytopGen;
import universalelectricity.BasicComponents;
import universalelectricity.network.PacketManager;
import basicpipes.BasicPipesMain;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
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
@Mod(modid = "SteamPower", name = "Steam Power", version = "0.0.10")
@NetworkMod(channels = { "SPpack" }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)

public class SteamPowerMain{	 
	static Configuration config = new Configuration((new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), "/UniversalElectricity/SteamPower.cfg")));
	private static int BlockID= configurationProperties();
	public static int EngineItemID;
	public static int EngineID;
	public static int genID;
	public static int genOutput;
	public static int steamOutBoiler;
	public static int pipeLoss;
	public static int boilerHeat;
	public static int fireOutput;
	public static final String channel = "SPpack";
	public static Block machine = new BlockMachine(BlockID).setBlockName("machine");
	public static Block engine = new BlockSteamPiston(EngineID).setBlockName("SteamEngien");
	public static Block gen = new BlockGenerator(genID).setBlockName("ElecGen");
	public static Item itemEngine = new ItemEngine(EngineItemID).setItemName("SteamEngine");
	
    public static SteamPowerMain instance;
    
    @SidedProxy(clientSide = "steampower.SteamClientProxy", serverSide = "steampower.SteamProxy")
    public static SteamProxy proxy;
	public static String textureFile = "/textures/";
	 public static int configurationProperties()
     {
             config.load(); 
             BlockID = Integer.parseInt(config.getOrCreateIntProperty("MachinesID", Configuration.CATEGORY_BLOCK, 3030).value);
             EngineItemID = Integer.parseInt(config.getOrCreateIntProperty("EngineItem", Configuration.CATEGORY_ITEM, 30308).value);
             EngineID = Integer.parseInt(config.getOrCreateIntProperty("SteamEngineID", Configuration.CATEGORY_BLOCK, 3031).value);  
             genID = Integer.parseInt(config.getOrCreateIntProperty("ElecGenID", Configuration.CATEGORY_BLOCK, 3032).value); 
             genOutput = Integer.parseInt(config.getOrCreateIntProperty("genOutputWattsmax", Configuration.CATEGORY_GENERAL, 1000).value);
             steamOutBoiler = Integer.parseInt(config.getOrCreateIntProperty("steamOutPerCycle", Configuration.CATEGORY_GENERAL, 10).value);
             boilerHeat = Integer.parseInt(config.getOrCreateIntProperty("boilerInKJNeed", Configuration.CATEGORY_GENERAL, 4500).value);
             fireOutput = Integer.parseInt(config.getOrCreateIntProperty("fireBoxOutKJMax", Configuration.CATEGORY_GENERAL,250).value);
             config.save();
             return BlockID;
     }
	 @PreInit
		public void preInit(FMLPreInitializationEvent event)
		{
		 instance = this;
		 NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		  proxy.preInit();
		  GameRegistry.registerBlock(machine, ItemMachine.class);
		  GameRegistry.registerBlock(engine);
		  GameRegistry.registerBlock(gen);
		}
	 @Init
		public void load(FMLInitializationEvent evt)
	    {
		 proxy.init();
		 GameRegistry.registerTileEntity(TileEntityNuller.class, "EUNuller");
		 GameRegistry.registerTileEntity(TileEntitytopGen.class, "gentop");
		//Names...............
		LanguageRegistry.addName((new ItemStack(machine, 1, 1)), "Boiler");
		LanguageRegistry.addName((new ItemStack(gen, 1, 0)), "Generator");
		LanguageRegistry.addName((new ItemStack(machine, 1, 2)), "FireBox");
		LanguageRegistry.addName((new ItemStack(itemEngine, 1, 0)), "SteamPiston");
		LanguageRegistry.addName((new ItemStack(machine, 1, 15)), "EUVampire");
	
		
	}
	 @PostInit
	 public void postInit(FMLPostInitializationEvent event)
		{
		 
		 proxy.postInit();
		 //Crafting
		try{
			/**
		  		case 0: return new TileEntityGrinder(); <-Removed
		        case 1: return new TileEntityBoiler();
		        case 2: return new TileEntityFireBox();
		        case 3: return new TileEntityGenerator();
		        case 14: return new TileEntityCondenser();<-Removed
		        case 15: return new TileEntityNuller();<-Just for testing Not craftable*/
		 
		GameRegistry.addRecipe(new ItemStack(machine, 1, 1), new Object [] {"@T@", "OVO", "@T@",
			'T',new ItemStack(BasicPipesMain.parts, 1,6),
			'@',new ItemStack(BasicComponents.itemSteelPlate),
			'O',new ItemStack(BasicPipesMain.parts, 1,0),
			'V',new ItemStack(BasicPipesMain.parts, 1,7)});
		GameRegistry.addRecipe(new ItemStack(machine, 1, 2), new Object [] { "@", "F",
			'F',Block.stoneOvenIdle,
			'@',new ItemStack(BasicComponents.itemSteelPlate)});
		GameRegistry.addRecipe(new ItemStack(itemEngine, 1,0), new Object [] {"@T@", "PMP", "@T@",
			'T',new ItemStack(BasicPipesMain.parts, 1,0),
			'@',new ItemStack(BasicComponents.itemSteelPlate),
			'P',Block.pistonBase,
			'M',new ItemStack(BasicComponents.itemMotor)});}
	catch(Exception e)
	{
	 e.printStackTrace();
	 System.out.print("UE based recipes not loaded");
	}
		}

}
