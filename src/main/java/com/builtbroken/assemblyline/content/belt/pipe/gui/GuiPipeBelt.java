package com.builtbroken.assemblyline.content.belt.pipe.gui;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.buttons.GuiButtonCheck;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/17/2017.
 */
public class GuiPipeBelt extends GuiContainerBase<TilePipeBelt>
{
    public static final ResourceLocation GUI_BUTTONS = new ResourceLocation(AssemblyLine.DOMAIN, "textures/gui/gui.buttons.32pix.png");

    public static int GUI_MAIN = 0;
    public static int GUI_SETTINGS = 1;
    public static int GUI_UPGRADES = 2;

    private GuiImageButton mainWindowButton;
    private GuiImageButton upgradeWindowButton;
    private GuiImageButton settingsWindowButton;

    private GuiButton2 onButton;
    private GuiButton2 offButton;


    GuiButtonCheck enableItemPullButton;
    GuiButtonCheck enableItemEjectButton;
    GuiButtonCheck renderTopButton;

    protected final int id;

    public GuiPipeBelt(EntityPlayer player, TilePipeBelt host, int id)
    {
        super(new ContainerPipeBelt(player, host, id), host);
        this.id = id;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int x = guiLeft - 18;
        int y = guiTop + 10;

        //Menu Tabs
        mainWindowButton = addButton(GuiImageButton.newButton18(GUI_MAIN, x, y, 0, 0).setTexture(GUI_BUTTONS));
        upgradeWindowButton = addButton(GuiImageButton.newButton18(GUI_UPGRADES, x, y + 19, 7, 0).setTexture(GUI_BUTTONS));
        settingsWindowButton = addButton(GuiImageButton.newButton18(GUI_SETTINGS, x, y + 19 * 2, 5, 0).setTexture(GUI_BUTTONS));

        //Power buttons
        onButton = (GuiButton2) add(GuiButton9px.newOnButton(10, x, y - 10).setEnabled(false));
        offButton = (GuiButton2) add(GuiButton9px.newOffButton(11, x + 9, y - 10).setEnabled(false));

        if (id == GUI_MAIN)
        {
            mainWindowButton.disable();
        }
        else if (id == GUI_SETTINGS)
        {
            settingsWindowButton.disable();

            x = guiLeft;
            y = guiTop;
            enableItemPullButton = add(new GuiButtonCheck(TilePipeBelt.BUTTON_ITEM_PULL, x + 10, y + 45, 1, host.pullItems));
            enableItemEjectButton = add(new GuiButtonCheck(TilePipeBelt.BUTTON_ITEM_EJECT, x + 10, y + 57, 1, host.shouldEjectItems));
            renderTopButton = add(new GuiButtonCheck(TilePipeBelt.BUTTON_RENDER_TOP, x + 10, y + 69, 1, !host.renderTop));
        }
        else if (id == GUI_UPGRADES)
        {
            upgradeWindowButton.disable();
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (id == GUI_SETTINGS)
        {
            enableItemPullButton.setChecked(host.pullItems);
            renderTopButton.setChecked(!host.renderTop);
            enableItemEjectButton.setChecked(host.shouldEjectItems);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button == mainWindowButton)
        {
            host.sendPacketToServer(host.getPacketForData(TilePipeBelt.PACKET_GUI_OPEN, GUI_MAIN));
        }
        else if (button == upgradeWindowButton)
        {
            host.sendPacketToServer(host.getPacketForData(TilePipeBelt.PACKET_GUI_OPEN, GUI_UPGRADES));
        }
        else if (button == settingsWindowButton)
        {
            host.sendPacketToServer(host.getPacketForData(TilePipeBelt.PACKET_GUI_OPEN, GUI_SETTINGS));
        }
        else if (id == GUI_SETTINGS)
        {
            if (button == enableItemPullButton)
            {
                enableItemPullButton.setChecked(!enableItemPullButton.isChecked());
                host.sendButtonEvent(button.id, enableItemPullButton.isChecked());
            }
            else if (button == renderTopButton)
            {
                renderTopButton.setChecked(!renderTopButton.isChecked());
                host.sendButtonEvent(button.id, !renderTopButton.isChecked());
            }
            else if (button == enableItemEjectButton)
            {
                enableItemEjectButton.setChecked(!enableItemEjectButton.isChecked());
                host.sendButtonEvent(button.id, enableItemEjectButton.isChecked());
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        final String tileName = "tile." + AssemblyLine.PREFIX + "belt.pipe.gui";
        drawStringCentered(LanguageUtility.getLocal(tileName), xSize / 2, 5);

        if (id == GUI_SETTINGS)
        {
            drawString(LanguageUtility.getLocal(tileName + ".button.item.pull"), 23, 46);
            drawString(LanguageUtility.getLocal(tileName + ".button.item.eject"), 23, 58);
            drawString(LanguageUtility.getLocal(tileName + ".button.render"), 23, 70);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        drawContainerSlots();
    }
}
