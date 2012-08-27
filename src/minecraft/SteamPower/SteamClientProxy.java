package SteamPower;


import net.minecraft.src.RenderEngine;
import net.minecraftforge.client.MinecraftForgeClient;
import SteamPower.boiler.TileEntityBoiler;
import SteamPower.burner.TileEntityFireBox;
import SteamPower.turbine.TileEntityGenerator;
import SteamPower.turbine.TileEntitytopGen;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class SteamClientProxy extends SteamProxy{

	public void preInit()
	{ 
		 MinecraftForgeClient.preloadTexture("/EUIClient/textures/blocks.png");
	   	 MinecraftForgeClient.preloadTexture("/EUIClient/textures/Items.png");
	}
	@Override
	public void init()
	{
		ClientRegistry.registerTileEntity(TileEntityBoiler.class, "boiler", new RenderBoiler(0f));
		ClientRegistry.registerTileEntity(TileEntityFireBox.class, "fireBox", new RenderFurnace());
		ClientRegistry.registerTileEntity(TileEntityGenerator.class, "generator", new RenderSteamEngine());
	}
	public void postInit()
	{
		
	}
	
}
