package basicpipes;
import java.io.File;

import basicpipes.pipes.BlockPipe;
import basicpipes.pipes.BlockMachine;
import basicpipes.pipes.ItemGuage;
import basicpipes.pipes.ItemParts;
import basicpipes.pipes.ItemPipe;
import basicpipes.pipes.TileEntityPump;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import universalelectricity.basiccomponents.BasicComponents;
import universalelectricity.network.PacketManager;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
@Mod(modid = "basicPipes", name = "Basic Pipes", version = "V4")
@NetworkMod(channels = { "Pipes" }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)

public class BasicPipesMain{	
	
    public BasicPipesMain instance;
    
    @SidedProxy(clientSide = "basicpipes.PipeClientProxy", serverSide = "basicpipes.PipeProxy")
	public static PipeProxy proxy;
	static Configuration config = new Configuration((new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), "/UniversalElectricity/BasicPipes.cfg")));
	public static int pipeID = configurationProperties();
	private static int partID;
	private static int ppipeID;
	private static int machineID;
	private static int toolID;
	public static Block pipe = new BlockPipe(pipeID).setBlockName("pipe");
	public static Block machine = new BlockMachine(machineID).setBlockName("pump");
	public static Item parts = new ItemParts(partID);
	public static Item itemPipes = new ItemPipe(ppipeID);
	public static Item gauge = new ItemGuage(toolID);

	public static String channel = "Pipes";
	public static String textureFile = "/textures";
	
	 public static int configurationProperties()
     {
             config.load();             
             pipeID = Integer.parseInt(config.getOrCreateIntProperty("PipeBlock", Configuration.CATEGORY_BLOCK, 155).value);
             machineID = Integer.parseInt(config.getOrCreateIntProperty("machineBlock", Configuration.CATEGORY_BLOCK, 156).value);
             partID = Integer.parseInt(config.getOrCreateIntProperty("parts", Configuration.CATEGORY_ITEM, 23022).value);
             ppipeID = Integer.parseInt(config.getOrCreateIntProperty("pipes", Configuration.CATEGORY_ITEM, 23023).value);
             toolID = Integer.parseInt(config.getOrCreateIntProperty("ToolID", Configuration.CATEGORY_ITEM, 23024).value);
             config.save();
             return pipeID;
     }
	 @PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
	  proxy.preInit();  
	  GameRegistry.registerBlock(pipe);
	  GameRegistry.registerBlock(machine);
	}
	@Init
	public void load(FMLInitializationEvent evt)
    {
		//register
		proxy.init();
		
   	    //Names
		LanguageRegistry.addName((new ItemStack(machine, 1, 0)), "WaterPump");
		LanguageRegistry.addName((new ItemStack(gauge, 1, 0)), "PipeGuage");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 0)), "SteamPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 1)), "WaterPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 2)), "LavaPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 3)), "OilPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 4)), "FuelPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 5)), "AirPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 6)), "MethainPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 7)), "BioFuelPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 8)), "coolentPipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 9)), "NukeWastePipe");
		LanguageRegistry.addName((new ItemStack(itemPipes, 1, 10)), "Pipe");
		LanguageRegistry.addName((new ItemStack(parts, 1, 0)), "BronzeTube");
		LanguageRegistry.addName((new ItemStack(parts, 1, 1)), "IronTube");		
		LanguageRegistry.addName((new ItemStack(parts, 1, 2)), "ObsidianTube");
		LanguageRegistry.addName((new ItemStack(parts, 1, 3)), "NetherTube");
		LanguageRegistry.addName((new ItemStack(parts, 1, 4)), "LeatherSeal");
		LanguageRegistry.addName((new ItemStack(parts, 1, 5)), "SlimeSeal");
		LanguageRegistry.addName((new ItemStack(parts, 1, 6)), "BronzeTank");
		LanguageRegistry.addName((new ItemStack(parts, 1, 7)), "Valve");
		//crafting parts
	}
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
		GameRegistry.addRecipe(new ItemStack(parts, 2,0), new Object[] { "@@@", '@',BasicComponents.itemBronzeIngot});//bronze tube
		GameRegistry.addRecipe(new ItemStack(parts, 2,1), new Object[] { "@@@", '@',Item.ingotIron});//iron tube
		GameRegistry.addRecipe(new ItemStack(parts, 2,2), new Object[] { "@@@", '@',Block.obsidian});//obby Tube
		GameRegistry.addRecipe(new ItemStack(parts, 2,3), new Object[] { "N@N", 'N',Block.netherrack,'@',new ItemStack(parts, 2,2)});//nether tube
		GameRegistry.addRecipe(new ItemStack(parts, 2,4), new Object[] { "@@","@@", '@',Item.leather});//seal		
		GameRegistry.addShapelessRecipe(new ItemStack(parts, 1,5), new Object[] { new ItemStack(parts, 1,4),new ItemStack(Item.slimeBall, 1)});//stick seal
		GameRegistry.addRecipe(new ItemStack(parts, 1,6), new Object[] { " @ ","@ @"," @ ", '@',BasicComponents.itemBronzeIngot});//tank
		GameRegistry.addRecipe(new ItemStack(parts, 1,7), new Object[] { "T@T", 'T',new ItemStack(parts,1,0),'@',Block.lever});//valve
		//crafting pipes	
		//{"black", "red", "green", "brown", "blue", "purple", "cyan", 
		//"silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
		//steam
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,0), new Object[] { new ItemStack(parts, 1,0),new ItemStack(parts, 1,4)});
		//water
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,1), new Object[] { new ItemStack(parts, 1,1),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,4)});
		//lava  TODO change to use obby pipe and nether items
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,2), new Object[] { new ItemStack(parts, 1,2),new ItemStack(Item.dyePowder, 1,1)});
		//oil
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,3), new Object[] { new ItemStack(parts, 1,1),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,0)});
		//fuel
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,4), new Object[] { new ItemStack(parts, 1,1),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,11)});
		
		//pump
		GameRegistry.addRecipe(new ItemStack(machine, 1,0), new Object[] { "@T@","BPB","@M@"
			, '@',BasicComponents.itemSteelPlate
			, 'M',BasicComponents.itemMotor
			, 'B',new ItemStack(parts, 1,7)
			, 'P',new ItemStack(Block.pistonBase)
			, 'C',BasicComponents.blockCopperWire
			, 'T',new ItemStack(parts, 1,6)
			});
	}

}
