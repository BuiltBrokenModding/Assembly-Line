package steampower;


import net.minecraftforge.client.MinecraftForgeClient;
import steampower.boiler.TileEntityBoiler;
import steampower.burner.TileEntityFireBox;
import steampower.geared.RenderGearPiston;
import steampower.turbine.TileEntityGen;
import steampower.turbine.TileEntitySteamPiston;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

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
