package dark.fluid.client;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.fluid.client.render.BlockRenderHelper;
import dark.fluid.client.render.ItemRenderHelper;
import dark.fluid.client.render.RenderConstructionPump;
import dark.fluid.client.render.RenderPump;
import dark.fluid.client.render.RenderReleaseValve;
import dark.fluid.client.render.RenderSink;
import dark.fluid.client.render.RenderTank;
import dark.fluid.client.render.pipe.RenderPipe;
import dark.fluid.common.CommonProxy;
import dark.fluid.common.FMRecipeLoader;
import dark.fluid.common.FluidMech;
import dark.fluid.common.machines.TileEntityReleaseValve;
import dark.fluid.common.machines.TileEntitySink;
import dark.fluid.common.machines.TileEntityTank;
import dark.fluid.common.pipes.TileEntityGenericPipe;
import dark.fluid.common.pipes.TileEntityPipe;
import dark.fluid.common.pump.TileEntityConstructionPump;
import dark.fluid.common.pump.TileEntityStarterPump;

@SideOnly(Side.CLIENT)
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
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGenericPipe.class, new RenderPipe());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStarterPump.class, new RenderPump());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRod.class, new RenderGearRod());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGenerator.class, new RenderGenerator());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReleaseValve.class, new RenderReleaseValve());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySink.class, new RenderSink());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConstructionPump.class, new RenderConstructionPump());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new RenderTank());

        MinecraftForgeClient.registerItemRenderer(FMRecipeLoader.blockPipe.blockID, new ItemRenderHelper());
        MinecraftForgeClient.registerItemRenderer(FMRecipeLoader.blockGenPipe.blockID, new ItemRenderHelper());
        MinecraftForgeClient.registerItemRenderer(FMRecipeLoader.blockReleaseValve.blockID, new ItemRenderHelper());

        RenderingRegistry.registerBlockHandler(new BlockRenderHelper());
    }

    @Override
    public void postInit()
    {

    }
}
