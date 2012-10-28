package assemblyline;

import net.minecraftforge.client.MinecraftForgeClient;
import assemblyline.belts.TileEntityConveyorBelt;
import assemblyline.machines.TileEntityManipulator;
import assemblyline.machines.TileEntitySorter;
import assemblyline.render.RenderConveyorBelt;
import assemblyline.render.RenderHelper;
import assemblyline.render.RenderManipulator;
import assemblyline.render.RenderSorter;
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySorter.class, new RenderSorter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityManipulator.class, new RenderManipulator());
	}

}
