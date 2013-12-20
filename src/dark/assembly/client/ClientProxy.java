package dark.assembly.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.ALRecipeLoader;
import dark.assembly.CommonProxy;
import dark.assembly.client.gui.GuiEncoderCoder;
import dark.assembly.client.gui.GuiEncoderHelp;
import dark.assembly.client.gui.GuiEncoderInventory;
import dark.assembly.client.gui.GuiImprinter;
import dark.assembly.client.gui.GuiProcessor;
import dark.assembly.client.render.BlockRenderHelper;
import dark.assembly.client.render.BlockRenderingHandler;
import dark.assembly.client.render.ItemPipeRenderer;
import dark.assembly.client.render.ItemRenderFluidCan;
import dark.assembly.client.render.ItemTankRenderer;
import dark.assembly.client.render.RenderConstructionPump;
import dark.assembly.client.render.RenderPipe;
import dark.assembly.client.render.RenderPump;
import dark.assembly.client.render.RenderReleaseValve;
import dark.assembly.client.render.RenderSink;
import dark.assembly.client.render.RenderTank;
import dark.assembly.client.render.RenderTurkey;
import dark.assembly.entities.EntityFarmEgg;
import dark.assembly.entities.EntityTurkey;
import dark.assembly.imprinter.TileEntityImprinter;
import dark.assembly.machine.encoder.TileEntityEncoder;
import dark.assembly.machine.processor.TileEntityProcessor;
import dark.fluid.common.machines.TileEntityReleaseValve;
import dark.fluid.common.machines.TileEntitySink;
import dark.fluid.common.machines.TileEntityTank;
import dark.fluid.common.pipes.TileEntityPipe;
import dark.fluid.common.pump.TileEntityConstructionPump;
import dark.fluid.common.pump.TileEntityStarterPump;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{

    @Override
    public void preInit()
    {
        RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
        MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
        RenderingRegistry.registerEntityRenderingHandler(EntityTurkey.class, new RenderTurkey());
        RenderingRegistry.registerEntityRenderingHandler(EntityFarmEgg.class, new RenderSnowball(Item.egg));
    }

    @Override
    public void init()
    {
        super.init();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new RenderPipe());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStarterPump.class, new RenderPump());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRod.class, new RenderGearRod());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGenerator.class, new RenderGenerator());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReleaseValve.class, new RenderReleaseValve());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySink.class, new RenderSink());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConstructionPump.class, new RenderConstructionPump());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new RenderTank());

        MinecraftForgeClient.registerItemRenderer(ALRecipeLoader.blockPipe.blockID, new ItemPipeRenderer());
        MinecraftForgeClient.registerItemRenderer(ALRecipeLoader.blockTank.blockID, new ItemTankRenderer());
        MinecraftForgeClient.registerItemRenderer(ALRecipeLoader.blockReleaseValve.blockID, new ItemPipeRenderer());

        RenderingRegistry.registerBlockHandler(new BlockRenderHelper());
        if (ALRecipeLoader.itemFluidCan != null)
            MinecraftForgeClient.registerItemRenderer(ALRecipeLoader.itemFluidCan.itemID, new ItemRenderFluidCan());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity != null)
        {
            switch (ID)
            {
                case GUI_IMPRINTER:
                {
                    return new GuiImprinter(player.inventory, (TileEntityImprinter) tileEntity);
                }
                case GUI_ENCODER:
                {
                    return new GuiEncoderInventory(player.inventory, (TileEntityEncoder) tileEntity);
                }
                case GUI_ENCODER_CODE:
                {
                    return new GuiEncoderCoder(player.inventory, (TileEntityEncoder) tileEntity);
                }
                case GUI_ENCODER_HELP:
                {
                    return new GuiEncoderHelp(player.inventory, (TileEntityEncoder) tileEntity);
                }
                case GUI_PROCESSOR:
                {
                    return new GuiProcessor(player.inventory, (TileEntityProcessor) tileEntity);
                }
            }
        }

        return null;
    }

    @Override
    public boolean isCtrKeyDown()
    {
        return GuiScreen.isCtrlKeyDown();
    }

}
