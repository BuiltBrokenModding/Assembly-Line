package net.minecraft.src;
import net.minecraft.client.Minecraft;
import net.minecraft.src.basiccomponents.BasicComponents;
import net.minecraft.src.eui.*;
import net.minecraft.src.eui.robotics.ModelModelShoeBot;
import net.minecraft.src.eui.robotics.RenderShoeBot;
import net.minecraft.src.forge.*;
import net.minecraft.src.universalelectricity.*;

import java.util.ArrayList;
import java.util.Map;
import java.io.*;
public class mod_automation extends NetworkMod {	 
	static Configuration config = new Configuration((new File(Minecraft.getMinecraftDir(), "config/EUIndustry/SteamPower.cfg")));
	public static int spawnItemId = configurationProperties();
	public static Item spawnItem = (new net.minecraft.src.eui.robotics.ItemSpawn(spawnItemId)).setItemName("Bot");
	@Override
	public String getVersion() {
		// TODO change version on each update ;/
		return "0.0.1";
	}
	 public static int configurationProperties()
     {
             config.load();             
             spawnItemId = Integer.parseInt(config.getOrCreateIntProperty("BotItem", Configuration.CATEGORY_ITEM, 31356).value);
             config.save();
             return spawnItemId;
     }
	@Override
	public void load() {
		//register
		UniversalElectricity.registerAddon(this, "0.4.5");
   	    MinecraftForgeClient.preloadTexture("/eui/Blocks.png");
   	    MinecraftForgeClient.preloadTexture("/eui/Items.png");
		ModLoader.addName((new ItemStack(spawnItem, 1, 0)), "Bot");
		//...........
		ModLoader.registerEntityID(net.minecraft.src.eui.robotics.EntityShoeBot.class, "Bot", 101);
		
	}
	@Override
	public void addRenderer(Map map) 
	{ 	     
	    map.put(net.minecraft.src.eui.robotics.EntityShoeBot.class, new RenderShoeBot());
	}
	

}
