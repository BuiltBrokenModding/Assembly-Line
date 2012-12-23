package dark.BasicUtilities;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dark.BasicUtilities.machines.TileEntityPump;
import dark.BasicUtilities.mechanical.TileEntityRod;
import dark.BasicUtilities.pipes.TileEntityPipe;
import dark.BasicUtilities.renders.RenderGearRod;
import dark.BasicUtilities.renders.RenderLTank;
import dark.BasicUtilities.renders.RenderPipe;
import dark.BasicUtilities.renders.RenderPump;
import dark.BasicUtilities.tanks.TileEntityLTank;

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
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLTank.class, new RenderLTank());
        RenderingRegistry.registerBlockHandler(new ItemRenderHelper());
    }

    @Override
    public void postInit()
    {

    }
}
