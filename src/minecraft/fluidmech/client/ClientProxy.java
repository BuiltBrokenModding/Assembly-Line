package fluidmech.client;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import fluidmech.client.render.BlockRenderHelper;
import fluidmech.client.render.ItemRenderHelper;
import fluidmech.client.render.RenderGearRod;
import fluidmech.client.render.RenderGenerator;
import fluidmech.client.render.RenderPump;
import fluidmech.client.render.RenderReleaseValve;
import fluidmech.client.render.RenderSink;
import fluidmech.client.render.RenderTank;
import fluidmech.client.render.pipeextentions.RenderPipe;
import fluidmech.common.CommonProxy;
import fluidmech.common.FluidMech;
import fluidmech.common.machines.mech.TileEntityGenerator;
import fluidmech.common.machines.mech.TileEntityRod;
import fluidmech.common.machines.pipes.TileEntityPipe;
import fluidmech.common.pump.TileEntityStarterPump;
import fluidmech.common.tiles.TileEntityReleaseValve;
import fluidmech.common.tiles.TileEntitySink;
import fluidmech.common.tiles.TileEntityTank;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		
	}

	@Override
	public void Init()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new RenderPipe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new RenderPipe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStarterPump.class, new RenderPump());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRod.class, new RenderGearRod());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGenerator.class, new RenderGenerator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new RenderTank());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReleaseValve.class, new RenderReleaseValve());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySink.class, new RenderSink());
		
		MinecraftForgeClient.registerItemRenderer(FluidMech.blockPipe.blockID, new ItemRenderHelper());
		MinecraftForgeClient.registerItemRenderer(FluidMech.blockReleaseValve.blockID, new ItemRenderHelper());
		
		RenderingRegistry.registerBlockHandler(new BlockRenderHelper());
	}

	@Override
	public void postInit()
	{

	}
}
