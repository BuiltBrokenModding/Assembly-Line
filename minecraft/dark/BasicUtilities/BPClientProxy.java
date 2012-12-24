package dark.BasicUtilities;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dark.BasicUtilities.Tile.TileEntityGen;
import dark.BasicUtilities.Tile.TileEntityTank;
import dark.BasicUtilities.Tile.TileEntityPipe;
import dark.BasicUtilities.Tile.TileEntityPump;
import dark.BasicUtilities.Tile.TileEntityRod;
import dark.BasicUtilities.renders.RenderGearRod;
import dark.BasicUtilities.renders.RenderGenerator;
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
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGen.class, new RenderGenerator());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new RenderTank());
        RenderingRegistry.registerBlockHandler(new BlockRenderHelper());
        MinecraftForgeClient.registerItemRenderer(BasicUtilitiesMain.itemPipes.shiftedIndex, new ItemRenderHelper());
        MinecraftForgeClient.registerItemRenderer(BasicUtilitiesMain.itemTank.shiftedIndex, new ItemRenderHelper());
    }

    @Override
    public void postInit()
    {

    }
}
