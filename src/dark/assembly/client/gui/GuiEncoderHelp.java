package dark.assembly.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import dark.assembly.AssemblyLine;
import dark.assembly.machine.encoder.TileEntityEncoder;

public class GuiEncoderHelp extends GuiEncoderBase
{
    public static final ResourceLocation TEXTURE_CODE_BACK = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "gui_encoder_coder.png");

    public GuiEncoderHelp(InventoryPlayer player, TileEntityEncoder tileEntity)
    {
        super(player, tileEntity);
    }
}
