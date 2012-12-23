package dark.BasicUtilities;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dark.BasicUtilities.Tile.TileEntityTank;
import dark.BasicUtilities.Tile.TileEntityPipe;
import dark.BasicUtilities.Tile.TileEntityPump;
import dark.BasicUtilities.Tile.TileEntityRod;
import dark.BasicUtilities.renders.RenderGearRod;
import dark.BasicUtilities.renders.RenderTank;
import dark.BasicUtilities.renders.RenderPipe;
import dark.BasicUtilities.renders.RenderPump;

public class BPClientProxy extends BPCommonProxy
{
    @Override
    public void preInit()
    {

    }

    @Override
    public void Init()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new RenderPipe());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPump.class, new RenderPump());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRod.class, new RenderGearRod());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new RenderTank());
        RenderingRegistry.registerBlockHandler(new ItemRenderHelper());
    }

    @Override
    public void postInit()
    {

    }
}
