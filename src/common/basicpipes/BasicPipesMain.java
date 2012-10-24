package basicpipes;
import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import universalelectricity.BasicComponents;
import universalelectricity.network.PacketManager;
import basicpipes.LTanks.ItemTank;
import basicpipes.LTanks.TileEntityLTank;
import basicpipes.conductors.BlockPipe;
import basicpipes.conductors.BlockRod;
import basicpipes.conductors.ItemGuage;
import basicpipes.conductors.ItemParts;
import basicpipes.conductors.ItemPipe;
import basicpipes.conductors.TileEntityPipe;
import basicpipes.conductors.TileEntityRod;
import basicpipes.machines.BlockMachine;
import basicpipes.machines.BlockValve;
import basicpipes.machines.TileEntityPump;
import basicpipes.pipes.api.Liquid;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
@Mod(modid = "basicPipes", name = "Basic Pipes", version = "1.9",dependencies = "after:UniversalElectricity")
@NetworkMod(channels = { "Pipes" }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)

public class BasicPipesMain{	
	
    public BasicPipesMain instance;
    
    @SidedProxy(clientSide = "basicpipes.PipeClientProxy", serverSide = "basicpipes.PipeProxy")
	public static PipeProxy proxy;
	static Configuration config = new Configuration((new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), "/UniversalElectricity/BasicPipes.cfg")));
	public static int pipeID = configurationProperties();
	private static int partID;
	private static int ppipeID;
	public static int machineID;
	private static int toolID;
	private static int tankID;
	public static int valveID;
	public static int rodID;
	public static Block pipe = new BlockPipe(pipeID).setBlockName("pipe");
	public static Block machine = new BlockMachine(machineID).setBlockName("pump");
	public static Block valve = new BlockValve(valveID).setBlockName("valve");
	public static Block rod = new BlockRod(rodID);
	public static Item parts = new ItemParts(partID);
	public static Item itemPipes = new ItemPipe(ppipeID);
	public static Item itemTank = new ItemTank(tankID);
	public static Item gauge = new ItemGuage(toolID);

	public static String channel = "Pipes";
	public static String textureFile = "/textures";
	public static boolean ueLoaded = false;
	
	
	 public static int configurationProperties()
     {
             config.load();             
             pipeID = Integer.parseInt(config.getOrCreateIntProperty("PipeBlock", Configuration.CATEGORY_BLOCK, 155).value);
             machineID = Integer.parseInt(config.getOrCreateIntProperty("machineBlock", Configuration.CATEGORY_BLOCK, 156).value);
             valveID = Integer.parseInt(config.getOrCreateIntProperty("ValveBlock", Configuration.CATEGORY_BLOCK, 157).value);
             rodID = Integer.parseInt(config.getOrCreateIntProperty("gearBlock", Configuration.CATEGORY_BLOCK, 158).value);
             partID = Integer.parseInt(config.getOrCreateIntProperty("parts", Configuration.CATEGORY_ITEM, 23022).value);
             ppipeID = Integer.parseInt(config.getOrCreateIntProperty("pipes", Configuration.CATEGORY_ITEM, 23023).value);
             toolID = Integer.parseInt(config.getOrCreateIntProperty("ToolID", Configuration.CATEGORY_ITEM, 23024).value);
             tankID = Integer.parseInt(config.getOrCreateIntProperty("tankID", Configuration.CATEGORY_ITEM, 23025).value);
             config.save();
             return pipeID;
     }
	 @PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
	  proxy.preInit();  
	  GameRegistry.registerBlock(pipe);
	  GameRegistry.registerBlock(rod);
	  GameRegistry.registerBlock(machine,basicpipes.machines.ItemMachine.class);
	}
	@Init
	public void load(FMLInitializationEvent evt)
    {
		//register
		proxy.init();
		GameRegistry.registerTileEntity(TileEntityPipe.class, "pipe");
		GameRegistry.registerTileEntity(TileEntityPump.class, "pump");
		GameRegistry.registerTileEntity(TileEntityRod.class, "rod");
		GameRegistry.registerTileEntity(TileEntityLTank.class, "ltank");
		//Names and lang stuff
			//Pipe Names
			for(int i =0; i < Liquid.values().length;i++)
			{
				LanguageRegistry.addName((new ItemStack(itemPipes, 1, i)), Liquid.getLiquid(i).lName+" Pipe");
			}
			for(int i =0; i < Liquid.values().length;i++)
			{
				LanguageRegistry.addName((new ItemStack(itemTank, 1, i)), Liquid.getLiquid(i).lName+" Tank");
			}
	   	    //Pump
			LanguageRegistry.addName((new ItemStack(machine, 1, 0)), "WaterPump");
			LanguageRegistry.addName((new ItemStack(machine, 1, 4)), "WaterCondensor");
			LanguageRegistry.addName((new ItemStack(rod, 1)), "MechRod");
			//Tools
			LanguageRegistry.addName((new ItemStack(gauge, 1, 0)), "PipeGuage");
			//Parts
			LanguageRegistry.addName((new ItemStack(parts, 1, 0)), "BronzeTube");
			LanguageRegistry.addName((new ItemStack(parts, 1, 1)), "IronTube");		
			LanguageRegistry.addName((new ItemStack(parts, 1, 2)), "ObsidianTube");
			LanguageRegistry.addName((new ItemStack(parts, 1, 3)), "NetherTube");
			LanguageRegistry.addName((new ItemStack(parts, 1, 4)), "LeatherSeal");
			LanguageRegistry.addName((new ItemStack(parts, 1, 5)), "SlimeSeal");
			LanguageRegistry.addName((new ItemStack(parts, 1, 6)), "BronzeTank");
			LanguageRegistry.addName((new ItemStack(parts, 1, 7)), "Valve");
	}
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
		GameRegistry.addRecipe(new ItemStack(parts, 2,1), new Object[] { "@@@", '@',Item.ingotIron});//iron tube
		GameRegistry.addRecipe(new ItemStack(parts, 2,2), new Object[] { "@@@", '@',Block.obsidian});//obby Tube
		GameRegistry.addRecipe(new ItemStack(parts, 2,3), new Object[] { "N@N", 'N',Block.netherrack,'@',new ItemStack(parts, 2,2)});//nether tube
		GameRegistry.addRecipe(new ItemStack(parts, 2,4), new Object[] { "@@","@@", '@',Item.leather});//seal		
		GameRegistry.addShapelessRecipe(new ItemStack(parts, 1,5), new Object[] { new ItemStack(parts, 1,4),new ItemStack(Item.slimeBall, 1)});//stick seal
		//crafting pipes	
		//{"black", "red", "green", "brown", "blue", "purple", "cyan", 
		//"silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
		GameRegistry.addRecipe(new ItemStack(rod, 1), new Object[] { "I@I", 'I',Item.ingotIron,'@',new ItemStack(parts, 1,1)});
		//water
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,1), new Object[] { new ItemStack(parts, 1,1),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,4)});
		//lava  TODO change to use obby pipe and nether items
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,2), new Object[] { new ItemStack(parts, 1,2),new ItemStack(Item.dyePowder, 1,1)});
		//oil
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,3), new Object[] { new ItemStack(parts, 1,1),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,0)});
		//fuel
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,4), new Object[] { new ItemStack(parts, 1,1),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,11)});
		GameRegistry.addRecipe(new ItemStack(parts, 1,7), new Object[] { "T@T", 'T',new ItemStack(parts,1,1),'@',Block.lever});//valve
		
		GameRegistry.addRecipe(new ItemStack(parts, 1,6), new Object[] { " @ ","@ @"," @ ", '@',Item.ingotIron});//tank
		GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1,0), new Object[] { new ItemStack(parts, 1,6),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,15)});
		GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1,1), new Object[] { new ItemStack(parts, 1,6),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,4)});
		//lava  TODO change to use obby pipe and nether items
		GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1,2), new Object[] { new ItemStack(parts, 1,6),Block.obsidian,Block.obsidian,Block.obsidian,Block.obsidian});
		//oil
		GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1,3), new Object[] { new ItemStack(parts, 1,6),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,0)});
		//fuel
		GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1,4), new Object[] { new ItemStack(parts, 1,6),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,11)});
		
		try{
		GameRegistry.addRecipe(new ItemStack(parts, 2,0), new Object[] { "@@@", '@',BasicComponents.itemBronzeIngot});//bronze tube
		//steam
		GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,0), new Object[] { new ItemStack(parts, 1,0),new ItemStack(parts, 1,4)});
		GameRegistry.addRecipe(new ItemStack(parts, 1,6), new Object[] { " @ ","@ @"," @ ", '@',BasicComponents.itemBronzeIngot});//tank
		
		//pump
		GameRegistry.addRecipe(new ItemStack(machine, 1,0), new Object[] { "@T@","BPB","@M@"
			, '@',BasicComponents.itemSteelPlate
			, 'M',BasicComponents.itemMotor
			, 'B',new ItemStack(parts, 1,7)
			, 'P',new ItemStack(Block.pistonBase)
			, 'C',BasicComponents.blockCopperWire
			, 'T',new ItemStack(parts, 1,6)
			});
		this.ueLoaded = true;
		}
		catch(Exception e)
		{
			System.out.print("UE based recipes not loaded");
			//secondary boiler tank
			
			//steam
			GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1,0), new Object[] { new ItemStack(parts, 1,1),new ItemStack(parts, 1,4)});
		}
	}

}
