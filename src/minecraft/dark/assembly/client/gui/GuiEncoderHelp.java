package dark.assembly.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.machine.encoder.TileEntityEncoder;

public class GuiEncoderHelp extends GuiEncoderBase
{
    public static final ResourceLocation TEXTURE_CODE_BACK = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "gui_encoder_coder.png");

    public GuiEncoderHelp(EntityPlayer player, TileEntityEncoder tileEntity)
    {
        super(player, tileEntity);
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawForegroundLayer(int x, int y, float var1)
    {
        String out = "Help for using the encoder is currently not ready";
        this.fontRenderer.drawString("\u00a77" + out, (int) (this.xSize / 2 - out.length() * 2.5), 20, 4210752);
    }
}
