package net.minecraft.src;
import net.minecraft.client.Minecraft;
import net.minecraft.src.basiccomponents.BasicComponents;
import net.minecraft.src.eui.*;
import net.minecraft.src.forge.*;
import net.minecraft.src.universalelectricity.*;

import java.util.ArrayList;
import java.util.Map;
import java.io.*;
public class mod_EUIndustry extends NetworkMod {	 
	static Configuration config = new Configuration((new File(Minecraft.getMinecraftDir(), "config/EUIndustry/SteamPower.cfg")));
	private static int coalID = configurationProperties();
	private static int BlockID;
	public static int pipeID;
	public static int partID;
	public static int pipeBlockID;
	public static int pipeBlockID2;
	public static int genOutput;
	public static int steamOutBoiler;
	public static int pipeLoss;
	public static int boilerHeat;
	public static int fireOutput;
	public static Block machine = new net.minecraft.src.eui.BlockMachine(BlockID).setBlockName("machine");
	public static Item coalNugget = new net.minecraft.src.eui.ItemCoalFuel(coalID);
	@Override
	public String getVersion() {
		// TODO change version on each update ;/
		return "0.0.6";
	}
	 public static int configurationProperties()
     {
             config.load();             
             coalID = Integer.parseInt(config.getOrCreateIntProperty("coalItems", Configuration.CATEGORY_ITEM, 30439).value);
             pipeBlockID = Integer.parseInt(config.getOrCreateIntProperty("PipeS", Configuration.CATEGORY_BLOCK, 128).value);
             pipeBlockID2 = Integer.parseInt(config.getOrCreateIntProperty("PipeW", Configuration.CATEGORY_BLOCK, 130).value);
             BlockID = Integer.parseInt(config.getOrCreateIntProperty("Machines", Configuration.CATEGORY_BLOCK, 129).value);             
             pipeID = Integer.parseInt(config.getOrCreateIntProperty("pipes", Configuration.CATEGORY_ITEM, 30433).value);
             partID = Integer.parseInt(config.getOrCreateIntProperty("parts", Configuration.CATEGORY_ITEM, 30434).value);
             genOutput = Integer.parseInt(config.getOrCreateIntProperty("genOutputWatts", Configuration.CATEGORY_GENERAL, 1000).value);
             steamOutBoiler = Integer.parseInt(config.getOrCreateIntProperty("steamOutBoiler", Configuration.CATEGORY_GENERAL, 10).value);
             boilerHeat = Integer.parseInt(config.getOrCreateIntProperty("boilerInKJ", Configuration.CATEGORY_GENERAL, 4500).value);
             fireOutput = Integer.parseInt(config.getOrCreateIntProperty("fireBoxOutKJ", Configuration.CATEGORY_GENERAL,50).value);
             config.save();
             return coalID;
     }
	@Override
	public void load() {
		//register
		UniversalElectricity.registerAddon(this, "0.4.3");
   	    MinecraftForgeClient.preloadTexture("/eui/Blocks.png");
   	    MinecraftForgeClient.preloadTexture("/eui/Items.png");
		//Blocks
   	    //gen
		//machine
		ModLoader.registerBlock(machine, net.minecraft.src.eui.ItemMachine.class);
		ModLoader.addName((new ItemStack(machine, 1, 0)), "CoalRefiner");
		ModLoader.addName((new ItemStack(machine, 1, 1)), "Boiler");
		ModLoader.addName((new ItemStack(machine, 1, 2)), "FireBox");
		ModLoader.addName((new ItemStack(machine, 1, 3)), "SteamGen");
		ModLoader.addName((new ItemStack(machine, 1, 14)), "WaterCondenser");
		ModLoader.addName((new ItemStack(machine, 1, 15)), "EUVampire");
		ModLoader.registerTileEntity(net.minecraft.src.eui.grinder.TileEntityGrinder.class, "grinder");
		ModLoader.registerTileEntity(net.minecraft.src.eui.boiler.TileEntityBoiler.class, "boiler");
		ModLoader.registerTileEntity(net.minecraft.src.eui.burner.TileEntityFireBox.class, "fireBox");
		ModLoader.registerTileEntity(net.minecraft.src.eui.turbine.TileEntityGenerator.class, "generator");
		ModLoader.registerTileEntity(net.minecraft.src.eui.TileEntityCondenser.class, "waterPump");
		ModLoader.registerTileEntity(net.minecraft.src.eui.TileEntityNuller.class, "EUNuller");
		//Items
		ModLoader.addName((new ItemStack(coalNugget, 1, 0)), "CoalNuggets");
		ModLoader.addName((new ItemStack(coalNugget, 1, 1)), "CoalPellets");
		ModLoader.addName((new ItemStack(coalNugget, 1, 2)), "CoalDust");
		//fuels
		//Crafting

		ModLoader.addRecipe(new ItemStack(machine, 1, 0), new Object [] {"@S@", "SCW", "@S@",
			'S',BasicComponents.ItemSteelIngot,
			'@',new ItemStack(BasicComponents.ItemSteelPlate),
			'C',new ItemStack(BasicComponents.ItemCircuit,1,1),
			'W',BasicComponents.ItemCopperWire});
		/**ModLoader.addRecipe(new ItemStack(machine, 1, 1), new Object [] {"@T@", "OVO", "@T@",
			'T',new ItemStack(parts, 1,1),
			'@',new ItemStack(UniversalComponents.ItemSteelPlate),
			'O',new ItemStack(parts, 1,4),
			'V',new ItemStack(parts, 1,3)});
		ModLoader.addRecipe(new ItemStack(machine, 1, 2), new Object [] { "@", "F",
			'F',Block.stoneOvenIdle,
			'@',new ItemStack(UniversalComponents.ItemSteelPlate)});
		ModLoader.addRecipe(new ItemStack(machine, 1, 3), new Object [] {"@T@", "PMP", "@T@",
			'T',new ItemStack(parts, 1,4),
			'@',new ItemStack(UniversalComponents.ItemSteelPlate),
			'P',Block.pistonBase,
			'M',new ItemStack(UniversalComponents.ItemMotor)});**/
		
		//smelting
		
	}
	

}
