package basicpipes;

import basicpipes.PipeProxy;
import basicpipes.pipes.TileEntityPipe;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;

public class PipeClientProxy extends PipeProxy
{
	@Override
	public void preInit()
	{
		//Preload textures
		MinecraftForgeClient.preloadTexture("/EUIClient/Textures/Items.png");
   	    MinecraftForgeClient.preloadTexture("/EUIClient/Textures/blocks.png");
	}
	
	@Override
	public void init()
	{
		ClientRegistry.registerTileEntity(TileEntityPipe.class, "pipe", new RenderPipe());
	}
}
