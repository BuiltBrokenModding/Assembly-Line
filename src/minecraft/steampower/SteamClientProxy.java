package steampower;


import net.minecraftforge.client.MinecraftForgeClient;
import steampower.boiler.TileEntityBoiler;
import steampower.burner.TileEntityFireBox;
import steampower.turbine.TileEntityGen;
import steampower.turbine.TileEntitySteamPiston;
import cpw.mods.fml.client.registry.ClientRegistry;

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
		ClientRegistry.registerTileEntity(TileEntitySteamPiston.class, "generator", new RenderSteamEngine());
		ClientRegistry.registerTileEntity(TileEntityGen.class, "elecGen", new RenderGenerator());
	}
	public void postInit()
	{
		
	}
	
}
