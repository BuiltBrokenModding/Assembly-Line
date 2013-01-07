package liquidmechanics.client;

import liquidmechanics.client.render.BlockRenderHelper;
import liquidmechanics.client.render.ItemRenderHelper;
import liquidmechanics.client.render.RenderGearRod;
import liquidmechanics.client.render.RenderGenerator;
import liquidmechanics.client.render.RenderPipe;
import liquidmechanics.client.render.RenderPump;
import liquidmechanics.client.render.RenderReleaseValve;
import liquidmechanics.client.render.RenderTank;
import liquidmechanics.common.CommonProxy;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.tileentity.TileEntityGenerator;
import liquidmechanics.common.tileentity.TileEntityPipe;
import liquidmechanics.common.tileentity.TileEntityPump;
import liquidmechanics.common.tileentity.TileEntityReleaseValve;
import liquidmechanics.common.tileentity.TileEntityRod;
import liquidmechanics.common.tileentity.TileEntityTank;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		MinecraftForgeClient.preloadTexture(LiquidMechanics.BLOCK_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(LiquidMechanics.ITEM_TEXTURE_FILE);
	}

	@Override
	public void Init()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new RenderPipe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPump.class, new RenderPump());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRod.class, new RenderGearRod());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGenerator.class, new RenderGenerator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new RenderTank());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReleaseValve.class, new RenderReleaseValve());
		MinecraftForgeClient.registerItemRenderer(LiquidMechanics.blockPipe.blockID, new ItemRenderHelper());
		MinecraftForgeClient.registerItemRenderer(LiquidMechanics.blockReleaseValve.blockID, new ItemRenderHelper());
		RenderingRegistry.registerBlockHandler(new BlockRenderHelper());
	}

	@Override
	public void postInit()
	{

	}
}
