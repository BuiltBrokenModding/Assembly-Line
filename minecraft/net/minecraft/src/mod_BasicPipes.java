package net.minecraft.src;
import net.minecraft.client.Minecraft;
import net.minecraft.src.eui.pipes.RenderPipe;
import net.minecraft.src.forge.*;
import net.minecraft.src.universalelectricity.*;
import net.minecraft.src.universalelectricity.components.UniversalComponents;

import java.util.ArrayList;
import java.util.Map;
import java.io.*;
public class mod_BasicPipes extends NetworkMod {	 
	static Configuration config = new Configuration((new File(Minecraft.getMinecraftDir(), "config/EUIndustry/BasicPipes.cfg")));
	public static int pipeID = configurationProperties();
	private static int partID;
	private static int ppipeID;
	public static Block pipe = new net.minecraft.src.eui.pipes.BlockPipe(pipeID).setBlockName("pipe");
	public static Item parts = new net.minecraft.src.eui.pipes.ItemParts(partID);
	public static Item itemPipes = new net.minecraft.src.eui.pipes.ItemPipe(ppipeID);
	public static Item gauge = new net.minecraft.src.eui.pipes.ItemGuage(ppipeID+1);
	@Override
	public String getVersion() {
		// TODO change version on each update ;/
		return "0.0.1";
	}
	 public static int configurationProperties()
     {
             config.load();             
             pipeID = Integer.parseInt(config.getOrCreateIntProperty("PipeBlock", Configuration.CATEGORY_BLOCK, 155).value);
             partID = Integer.parseInt(config.getOrCreateIntProperty("parts", Configuration.CATEGORY_ITEM, 23022).value);
             ppipeID = Integer.parseInt(config.getOrCreateIntProperty("pipes", Configuration.CATEGORY_ITEM, 23023).value);
             config.save();
             return pipeID;
     }
	@Override
	public void load() {
		//register
		UniversalElectricity.registerAddon(this, "0.3.1");
   	    MinecraftForgeClient.preloadTexture("/eui/Items.png");
   	    ModLoader.registerBlock(pipe);
		ModLoader.registerTileEntity(net.minecraft.src.eui.pipes.TileEntityPipe.class, "pipe", new RenderPipe());
   	    //Names
		ModLoader.addName((new ItemStack(gauge, 1, 0)), "guage");
		ModLoader.addName((new ItemStack(itemPipes, 1, 0)), "SteamPipe");
		ModLoader.addName((new ItemStack(itemPipes, 1, 1)), "WaterPipe");
		ModLoader.addName((new ItemStack(itemPipes, 1, 2)), "LavaPipe");
		ModLoader.addName((new ItemStack(itemPipes, 1, 3)), "OilPipe");
		ModLoader.addName((new ItemStack(itemPipes, 1, 4)), "FuelPipe");
		ModLoader.addName((new ItemStack(itemPipes, 1, 5)), "AirPipe");
		ModLoader.addName((new ItemStack(parts, 1, 0)), "BronzeTube");
		ModLoader.addName((new ItemStack(parts, 1, 1)), "IronTube");
		ModLoader.addName((new ItemStack(parts, 1, 2)), "LeatherSeal");
		ModLoader.addName((new ItemStack(parts, 1, 3)), "ObsidianTube");
		ModLoader.addName((new ItemStack(parts, 1, 4)), "SlimeSeal");
		//Blocks
		
		ModLoader.addRecipe(new ItemStack(parts, 2,0), new Object[] { "@@@", '@',
			UniversalComponents.ItemBronzeIngot});
		ModLoader.addRecipe(new ItemStack(parts, 2,1), new Object[] { "@@@", '@',
			Block.obsidian});
		ModLoader.addRecipe(new ItemStack(parts, 2,2), new Object[] { "@@","@@", '@',
			Item.leather});
		ModLoader.addRecipe(new ItemStack(parts, 2,3), new Object[] { "@@@", '@',
			Item.ingotIron});
		ModLoader.addShapelessRecipe(new ItemStack(parts, 1,4), new Object[] { new ItemStack(parts, 1,2),new ItemStack(Item.slimeBall, 1)});
	       //crafting parts
		/**
		 * case 0: return "steam";
		case 1: return "water";
		case 2: return "lava";
		case 3: return "oil";
		case 4: return "fuel";
		case 5: return "air";
		 */
		ModLoader.addShapelessRecipe(new ItemStack(itemPipes, 1,0), new Object[] { new ItemStack(parts, 1,0),new ItemStack(parts, 1,2)});
		ModLoader.addShapelessRecipe(new ItemStack(itemPipes, 1,1), new Object[] { new ItemStack(parts, 1,0),new ItemStack(parts, 1,2),new ItemStack(Item.dyePowder, 1,4)});
		ModLoader.addShapelessRecipe(new ItemStack(itemPipes, 1,2), new Object[] { new ItemStack(parts, 1,1)});
		ModLoader.addShapelessRecipe(new ItemStack(itemPipes, 1,3), new Object[] { new ItemStack(parts, 1,3),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,0)});
		ModLoader.addShapelessRecipe(new ItemStack(itemPipes, 1,4), new Object[] { new ItemStack(parts, 1,3),new ItemStack(parts, 1,4),new ItemStack(Item.dyePowder, 1,11)});
		
	}
	

}
