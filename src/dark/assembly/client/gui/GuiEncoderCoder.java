package dark.assembly.client.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import dark.assembly.AssemblyLine;
import dark.assembly.machine.encoder.TileEntityEncoder;
import dark.core.prefab.invgui.GuiButtonImage;
import dark.core.prefab.invgui.GuiButtonImage.ButtonIcon;

public class GuiEncoderCoder extends GuiEncoderBase
{
    public static final ResourceLocation TEXTURE_CODE_BACK = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "gui_encoder_coder.png");
    private GuiTaskList taskListGui;
    GuiButtonImage left, right, up, down;

    public GuiEncoderCoder(InventoryPlayer player, TileEntityEncoder tileEntity)
    {
        super(player, tileEntity);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.getTaskListElement(true);
        left = new GuiButtonImage(3, containerWidth + 13, containerHeight + 140, ButtonIcon.ARROW_LEFT);
        this.buttonList.add(left);
        right = new GuiButtonImage(4, containerWidth + 147, containerHeight + 140, ButtonIcon.ARROW_RIGHT);
        this.buttonList.add(right);
        up = new GuiButtonImage(5, containerWidth + 147, containerHeight + 75, ButtonIcon.ARROW_UP);
        this.buttonList.add(up);
        down = new GuiButtonImage(6, containerWidth + 147, containerHeight + 90, ButtonIcon.ARROW_DOWN);
        this.buttonList.add(down);

    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        switch (button.id)
        {
            case 3:
                getTaskListElement().scrollSide(-1);
                break;
            case 4:
                getTaskListElement().scrollSide(1);
                break;
            case 5:
                getTaskListElement().scroll(-1);
                break;
            case 6:
                getTaskListElement().scroll(1);
                break;
        }
    }

    protected GuiTaskList getTaskListElement(boolean renew)
    {
        if (taskListGui == null || renew)
        {
            if (taskListGui != null)
            {
                taskListGui.xPos = this.containerWidth + 25;
                taskListGui.yPos = this.containerHeight + 15;
            }
            else
            {
                taskListGui = new GuiTaskList(this.tileEntity, this, this.containerWidth + 25, this.containerHeight + 15);
            }
        }
        return this.taskListGui;
    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel > 0)
        {
            this.getTaskListElement().scroll(-2);
        }
        else if (wheel < 0)
        {
            this.getTaskListElement().scroll(2);
        }
    }

    @Override
    protected void keyTyped(char character, int keycode)
    {
        if (keycode == Keyboard.KEY_ESCAPE)
        {
            this.mc.thePlayer.closeScreen();
        }
        else if (keycode == Keyboard.KEY_UP) // PAGE UP (no constant)
        {
            this.getTaskListElement().scroll(-1);
        }
        else if (keycode == Keyboard.KEY_DOWN) // PAGE DOWN (no constant)
        {
            this.getTaskListElement().scroll(1);
        }
        else if (keycode == Keyboard.KEY_LEFT) // PAGE LEFT (no constant)
        {
            if (this.getTaskListElement().scrollX > -5)
                this.getTaskListElement().scrollSide(-1);
        }
        else if (keycode == Keyboard.KEY_RIGHT) // PAGE RIGHT (no constant)
        {
            if (this.getTaskListElement().scrollX < ((TileEntityEncoder) tileEntity).getProgram().getSize().intX())
                this.getTaskListElement().scrollSide(1);
        }
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
