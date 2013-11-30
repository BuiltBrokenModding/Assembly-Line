package dark.assembly.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
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
    public void initGui()
    {
        super.initGui();
        this.getTaskListElement(true);

    }

    protected GuiTaskList getTaskListElement(boolean renew)
    {
        if (taskListGui == null || renew)
        {
            taskListGui = new GuiTaskList(this.tileEntity, this.containerWidth + 25, this.containerHeight + 15);
        }
        return this.taskListGui;
    }

    protected GuiTaskList getTaskListElement()
    {
        return this.getTaskListElement(false);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        getTaskListElement().drawConsole(this.mc);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        taskListGui.drawGuiContainerForegroundLayer(this.mc, x, y);
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
