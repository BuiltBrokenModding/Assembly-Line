package steampower;
import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import steampower.turbine.BlockGenerator;
import steampower.turbine.BlockSteamPiston;
import steampower.turbine.ItemEngine;
import steampower.turbine.TileEntitytopGen;
import universalelectricity.prefab.network.PacketManager;
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
@Mod(modid = "SteamPower", name = "Steam Power", version = "1.9",dependencies = "after:basicPipes")
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
             BlockID = Integer.parseInt(config.get(Configuration.CATEGORY_BLOCK,"MachinesID",  3030).value);
             EngineItemID = Integer.parseInt(config.get(Configuration.CATEGORY_ITEM,"EngineItem",  30308).value);
             EngineID = Integer.parseInt(config.get(Configuration.CATEGORY_BLOCK,"SteamEngineID",  3031).value);  
             genID = Integer.parseInt(config.get(Configuration.CATEGORY_BLOCK,"ElecGenID",  3032).value); 
             genOutput = Integer.parseInt(config.get(Configuration.CATEGORY_GENERAL,"genOutputWattsmax",  1000).value);
             steamOutBoiler = Integer.parseInt(config.get(Configuration.CATEGORY_GENERAL,"steamOutPerCycle",  10).value);
             boilerHeat = Integer.parseInt(config.get(Configuration.CATEGORY_GENERAL,"boilerInKJNeed",  4500).value);
             fireOutput = Integer.parseInt(config.get(Configuration.CATEGORY_GENERAL,"fireBoxOutKJMax", 250).value);
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
		 GameRegistry.registerTileEntity(TileEntitytopGen.class, "gentop");
		//Names...............
		LanguageRegistry.addName((new ItemStack(machine, 1, 1)), "Boiler");
		LanguageRegistry.addName((new ItemStack(gen, 1, 0)), "Generator");
		LanguageRegistry.addName((new ItemStack(machine, 1, 2)), "FireBox");
		LanguageRegistry.addName((new ItemStack(itemEngine, 1, 0)), "SteamPiston");
	
		
	}
	 @PostInit
	 public void postInit(FMLPostInitializationEvent event)
		{
		 
		 proxy.postInit();
		 //Crafting
		try{
			CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(gen, 1), new Object [] {"@T@", "OVO", "@T@",
				'T',new ItemStack(BasicPipesMain.rod, 1),
				'@',"plateSteel",
				'O',"basicCircuit",
				'V',"motor"}));
			/**
		        TileEntityBoiler();<- metadata 1
		        TileEntityFireBox();<-metadata 2-5
		        */
			CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(
					new ItemStack(machine, 1, 1), new Object [] {"TT", "VV", "TT",
			'T',new ItemStack(BasicPipesMain.parts, 1,6),
			'V',new ItemStack(BasicPipesMain.parts, 1,7)}));
			CraftingManager.getInstance().getRecipeList().add(
					new ShapedOreRecipe(new ItemStack(machine, 1, 2), new Object [] { "@", "F",
			'F',Block.stoneOvenIdle,
			'@',"plateSteel"}));
			CraftingManager.getInstance().getRecipeList().add(
					new ShapedOreRecipe(new ItemStack(itemEngine, 1,0), new Object [] {"GGG", "VPV", "@T@",
			'T',new ItemStack(BasicPipesMain.parts, 1,1),
			'G',BasicPipesMain.rod,
			'@',"plateSteel",
			'P',Block.pistonBase,
			'V',new ItemStack(BasicPipesMain.parts, 1,7),
			'M',"motor"}));
			}
	catch(Exception e)
	{
	 e.printStackTrace();
	 System.out.print("UE based recipes not loaded");
	}
		}

}
