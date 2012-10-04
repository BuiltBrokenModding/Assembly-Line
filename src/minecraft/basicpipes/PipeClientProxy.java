package basicpipes;

import steampower.SteamPowerMain;
import basicpipes.PipeProxy;
import basicpipes.conductors.TileEntityPipe;
import basicpipes.conductors.TileEntityRod;
import basicpipes.machines.TileEntityPump;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class PipeClientProxy extends PipeProxy
{
	@Override
	public void preInit()
	{
		//Preload textures
		MinecraftForgeClient.preloadTexture(BasicPipesMain.textureFile+"/Items.png");
   	    MinecraftForgeClient.preloadTexture(BasicPipesMain.textureFile+"/blocks.png");
   	 RenderingRegistry.registerBlockHandler(new ItemRenderHelper());
	}
	
	@Override
	public void init()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new RenderPipe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPump.class, new RenderPump());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRod.class, new RenderGearRod());
	}
}
