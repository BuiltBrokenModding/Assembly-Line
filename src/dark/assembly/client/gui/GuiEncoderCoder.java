package dark.assembly.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import dark.assembly.AssemblyLine;
import dark.assembly.machine.encoder.TileEntityEncoder;

public class GuiEncoderCoder extends GuiEncoderBase
{
    public static final ResourceLocation TEXTURE_CODE_BACK = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "gui_encoder_coder.png");
    private GuiTaskList taskListGui;

    public GuiEncoderCoder(InventoryPlayer player, TileEntityEncoder tileEntity)
    {
        super(player, tileEntity);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
    {
        if (taskListGui == null)
        {
            taskListGui = new GuiTaskList((this.width - this.xSize) / 2 + 25, (this.height - this.ySize) / 2 + 30);
        }
        super.drawGuiContainerBackgroundLayer(var1, x, y);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_CODE_BACK);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int containerWidth = (this.width - this.xSize) / 2;
        int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
        taskListGui.drawConsole();
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        if (par3 == 0)
        {
            this.taskListGui.mousePressed(par1, par2);
        }
    }

}
