package dark.assembly.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.CommonProxy;
import dark.assembly.armbot.TileEntityArmbot;
import dark.assembly.client.gui.GuiEncoderCoder;
import dark.assembly.client.gui.GuiEncoderHelp;
import dark.assembly.client.gui.GuiEncoderInventory;
import dark.assembly.client.gui.GuiImprinter;
import dark.assembly.client.gui.GuiProcessor;
import dark.assembly.client.render.BlockRenderingHandler;
import dark.assembly.client.render.RenderArmbot;
import dark.assembly.client.render.RenderConveyorBelt;
import dark.assembly.client.render.RenderCrate;
import dark.assembly.client.render.RenderDetector;
import dark.assembly.client.render.RenderManipulator;
import dark.assembly.client.render.RenderProcessor;
import dark.assembly.client.render.RenderRejector;
import dark.assembly.imprinter.TileEntityImprinter;
import dark.assembly.machine.TileEntityCrate;
import dark.assembly.machine.TileEntityDetector;
import dark.assembly.machine.TileEntityManipulator;
import dark.assembly.machine.TileEntityRejector;
import dark.assembly.machine.belt.TileEntityConveyorBelt;
import dark.assembly.machine.encoder.TileEntityEncoder;
import dark.assembly.machine.processor.TileEntityProcessor;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{

    @Override
    public void preInit()
    {
        RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
        MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
    }

    @Override
    public void init()
    {
        super.init();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorBelt.class, new RenderConveyorBelt());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRejector.class, new RenderRejector());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProcessor.class, new RenderProcessor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityManipulator.class, new RenderManipulator());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrate.class, new RenderCrate());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmbot.class, new RenderArmbot());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDetector.class, new RenderDetector());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraneController.class, new RenderCraneController());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraneRail.class, new RenderCraneFrame());
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
                    return new GuiEncoderCoder(player, (TileEntityEncoder) tileEntity);
                }
                case GUI_ENCODER_HELP:
                {
                    return new GuiEncoderHelp(player, (TileEntityEncoder) tileEntity);
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
