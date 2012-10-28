package assemblyline;

import net.minecraftforge.client.MinecraftForgeClient;
import assemblyline.AssemblyLine;
import assemblyline.ALCommonProxy;
import assemblyline.belts.TileEntityConveyorBelt;
import assemblyline.interaction.TileEntityEjector;
import assemblyline.interaction.TileEntityMachineInput;
import assemblyline.render.BeltRenderHelper;
import assemblyline.render.RenderBeltMain;
import assemblyline.render.RenderMachineBelt;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ALClientProxy extends ALCommonProxy
{
	@Override
	public void preInit()
	{ 
		MinecraftForgeClient.preloadTexture(AssemblyLine.TEXTURE_PATH+"/Items.png");
		RenderingRegistry.registerBlockHandler(new BeltRenderHelper());
	}
	@Override
	public void init()
	{
		//ClientRegistry.registerTileEntity(TileEntityConveyorBelt.class, "belt", new RenderConveyorBelt());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorBelt.class, new RenderBeltMain());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEjector.class,new RenderEjector());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachineInput.class,new RenderMachineBelt());
	}
	
}
