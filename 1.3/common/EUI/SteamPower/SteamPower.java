package EUI.SteamPower;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;

import java.util.ArrayList;
import java.util.Map;
import java.io.*;

import EUI.BasicPipes.BasicPipes;
import EUI.SteamPower.boiler.TileEntityBoiler;
import EUI.SteamPower.burner.TileEntityFireBox;
import EUI.SteamPower.turbine.TileEntityGenerator;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import universalelectricity.UniversalElectricity;
import universalelectricity.basiccomponents.BasicComponents;
import universalelectricity.network.PacketManager;
@Mod(modid = "SteamPower", name = "Steam Power", version = "V4")
@NetworkMod(channels = { "SPpack" }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)

public class SteamPower{	 
	static Configuration config = new Configuration((new File(Minecraft.getMinecraftDir(), "config/EUIndustry/SteamPower.cfg")));
	private static int BlockID= configurationProperties();
	public static int genOutput;
	public static int steamOutBoiler;
	public static int pipeLoss;
	public static int boilerHeat;
	public static int fireOutput;
	public static final String channel = "SPpack";
	public static Block machine = new EUI.SteamPower.BlockMachine(BlockID).setBlockName("machine");
	@Instance
    public static SteamPower instance;
    
    @SidedProxy(clientSide = "EUIClient.SteamPower.SteamClientProxy", serverSide = "EUI.SteamPower.SteamProxy")
    public static SteamProxy proxy;
	public static String textureFile = "/EUIClient/Textures/";
	 public static int configurationProperties()
     {
             config.load(); 
             BlockID = Integer.parseInt(config.getOrCreateIntProperty("MachinesID", Configuration.CATEGORY_BLOCK, 3030).value);
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
		}
	 @Init
		public void load(FMLInitializationEvent evt)
	    {
		 proxy.init();
		 GameRegistry.registerTileEntity(TileEntityNuller.class, "EUNuller");
		//Names...............
		LanguageRegistry.addName((new ItemStack(machine, 1, 1)), "Boiler");
		LanguageRegistry.addName((new ItemStack(machine, 1, 2)), "FireBox");
		LanguageRegistry.addName((new ItemStack(machine, 1, 3)), "SteamGen");
		LanguageRegistry.addName((new ItemStack(machine, 1, 15)), "EUVampire");
	
		
	}
	 @PostInit
	 public void postInit(FMLPostInitializationEvent event)
		{
		 proxy.postInit();
		 UniversalElectricity.registerMod(this,"SteamPower", "0.5.1");	
		 //Crafting
		/**
		 *  	case 0: return new TileEntityGrinder(); <-Removed
		        case 1: return new TileEntityBoiler();
		        case 2: return new TileEntityFireBox();
		        case 3: return new TileEntityGenerator();
		        case 14: return new TileEntityCondenser();<-Removed
		        case 15: return new TileEntityNuller();<-Just for testing Not craftable
		 */
		GameRegistry.addRecipe(new ItemStack(machine, 1, 1), new Object [] {"@T@", "OVO", "@T@",
			'T',new ItemStack(BasicPipes.parts, 1,5),
			'@',new ItemStack(BasicComponents.itemSteelPlate),
			'O',new ItemStack(BasicPipes.parts, 1,1),
			'V',new ItemStack(BasicPipes.parts, 1,6)});
		GameRegistry.addRecipe(new ItemStack(machine, 1, 2), new Object [] { "@", "F",
			'F',Block.stoneOvenIdle,
			'@',new ItemStack(BasicComponents.itemSteelPlate)});
		GameRegistry.addRecipe(new ItemStack(machine, 1, 3), new Object [] {"@T@", "PMP", "@T@",
			'T',new ItemStack(BasicPipes.parts, 1,0),
			'@',new ItemStack(BasicComponents.itemSteelPlate),
			'P',Block.pistonBase,
			'M',new ItemStack(BasicComponents.itemMotor)});
		}

}
