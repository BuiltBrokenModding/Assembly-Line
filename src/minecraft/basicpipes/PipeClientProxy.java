package basicpipes;

import steampower.SteamPowerMain;
import basicpipes.PipeProxy;
import basicpipes.pipes.TileEntityPipe;
import basicpipes.pumps.TileEntityPump;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class PipeClientProxy extends PipeProxy
{
	@Override
	public void preInit()
	{
		//Preload textures
		MinecraftForgeClient.preloadTexture(BasicPipesMain.textureFile+"/Items.png");
   	    MinecraftForgeClient.preloadTexture(BasicPipesMain.textureFile+"/blocks.png");
	}
	
	@Override
	public void init()
	{
		ClientRegistry.registerTileEntity(TileEntityPipe.class, "pipe", new RenderPipe());
		ClientRegistry.registerTileEntity(TileEntityPump.class, "pump", new RenderPump());
	}
}
