package assemblyline;

import net.minecraftforge.client.MinecraftForgeClient;
import assemblyline.AssemblyLine;
import assemblyline.ALCommonProxy;
import assemblyline.belts.TileEntityConveyorBelt;
import assemblyline.interaction.TileEntityEjector;
import assemblyline.interaction.TileEntityInjector;
import assemblyline.render.RenderEjector;
import assemblyline.render.RenderHelper;
import assemblyline.render.RenderConveyorBelt;
import assemblyline.render.RenderInjector;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ALClientProxy extends ALCommonProxy
{
	@Override
	public void preInit()
	{
		MinecraftForgeClient.preloadTexture(AssemblyLine.TEXTURE_PATH + "/Items.png");
		RenderingRegistry.registerBlockHandler(new RenderHelper());
	}

	@Override
	public void init()
	{
		// ClientRegistry.registerTileEntity(TileEntityConveyorBelt.class,
		// "belt", new RenderConveyorBelt());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorBelt.class, new RenderConveyorBelt());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEjector.class, new RenderEjector());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInjector.class, new RenderInjector());
	}

}
