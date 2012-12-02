package dark.SteamPower;


import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dark.BasicUtilities.mechanical.TileEntityGen;
import dark.SteamPower.boiler.TileEntityBoiler;
import dark.SteamPower.firebox.TileEntityFireBox;
import dark.SteamPower.renders.RenderBoiler;
import dark.SteamPower.renders.RenderFurnace;
import dark.SteamPower.renders.RenderGearPiston;
import dark.SteamPower.renders.RenderGenerator;
import dark.SteamPower.steamengine.TileEntitySteamPiston;

public class SteamClientProxy extends SteamProxy{

	public void preInit()
	{ 
		 MinecraftForgeClient.preloadTexture(SteamPowerMain.textureFile+"blocks.png");
	   	 MinecraftForgeClient.preloadTexture(SteamPowerMain.textureFile+"Items.png");
	   	 RenderingRegistry.registerBlockHandler(new ItemRenderHelperS());
	}
	@Override
	public void init()
	{
		ClientRegistry.registerTileEntity(TileEntityBoiler.class, "boiler", new RenderBoiler(0f));
		ClientRegistry.registerTileEntity(TileEntityFireBox.class, "fireBox", new RenderFurnace());
		ClientRegistry.registerTileEntity(TileEntitySteamPiston.class, "generator", new RenderGearPiston());
		ClientRegistry.registerTileEntity(TileEntityGen.class, "elecGen", new RenderGenerator());
	}
	public void postInit()
	{
		
	}
	
}
