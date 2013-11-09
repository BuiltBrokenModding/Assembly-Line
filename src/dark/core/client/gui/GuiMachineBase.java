package dark.core.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import dark.core.common.DarkMain;
import dark.core.prefab.invgui.GuiBase;
import dark.core.prefab.invgui.GuiButtonImage;
import dark.core.prefab.machine.TileEntityMachine;

/** To be used with all machine that have a gui to allow generic settings and feature all all devices
 * 
 * @author DarkGuardsman */
public class GuiMachineBase extends GuiBase
{

    public static final ResourceLocation TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, DarkMain.GUI_DIRECTORY + "gui_base_machine.png");

    protected static final int MAX_BUTTON_ID = 3;
    protected TileEntityMachine tileEntity;
    protected EntityPlayer entityPlayer;

    public GuiMachineBase(EntityPlayer player, TileEntityMachine tileEntity)
    {
        this.tileEntity = tileEntity;
        this.entityPlayer = player;
        this.guiSize.y = 380 / 2;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        // Inventory, Should be the Gui the machine opens to unless it has no inventory
        this.buttonList.add(new GuiButtonImage(0, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 0, 3));
        // Machine settings
        this.buttonList.add(new GuiButtonImage(1, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 22, 0));
        // About page, should display information about the machines power needs, help information, and tips
        this.buttonList.add(new GuiButtonImage(2, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 44, 2));

    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
            case 0:
            {
                //TODO open main GUI
                break;
            }
            case 1:
            {
                //TODO open second GUI
                break;
            }
            case 2:
            {
                //TODO open third GUI
                break;
            }
        }
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

}
