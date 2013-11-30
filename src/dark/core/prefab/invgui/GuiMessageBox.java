package dark.core.prefab.invgui;

import net.minecraft.client.gui.GuiButton;

public class GuiMessageBox extends GuiBase
{
    GuiBase returnGui;
    int id;
    String title;
    String message;

    public GuiMessageBox(GuiBase returnGui, int id, String title, String message)
    {
        this.guiSize.y = 380 / 2;
        this.returnGui = returnGui;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();

        this.buttonList.add(new GuiButton(0, (this.width - this.guiSize.intX()) / 2 + 13, (this.height - this.guiSize.intY()) / 2 + 135, 50, 20, "Yes"));

        this.buttonList.add(new GuiButton(1, (this.width - this.guiSize.intX()) / 2 + 68, (this.height - this.guiSize.intY()) / 2 + 135, 50, 20, "no"));

    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }

    @Override
    protected void drawForegroundLayer(int var2, int var3, float var1)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void drawBackgroundLayer(int var2, int var3, float var1)
    {
        // TODO Auto-generated method stub

    }

    public void show()
    {

    }

    public void exit(boolean yes)
    {
        if (this.returnGui instanceof IMessageBoxDialog)
        {
            ((IMessageBoxDialog) this.returnGui).onMessageBoxClosed(this.id, yes);
        }
    }
}
