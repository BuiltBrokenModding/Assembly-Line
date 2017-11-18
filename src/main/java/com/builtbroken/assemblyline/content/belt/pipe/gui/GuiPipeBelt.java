package com.builtbroken.assemblyline.content.belt.pipe.gui;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiButtonCheck;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/17/2017.
 */
public class GuiPipeBelt extends GuiContainerBase<TilePipeBelt>
{
    GuiButtonCheck enableItemPullButton;
    GuiButtonCheck enableItemEjectButton;
    GuiButtonCheck renderTopButton;

    public GuiPipeBelt(EntityPlayer player, TilePipeBelt host)
    {
        super(new ContainerPipeBelt(player, host), host);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int x = guiLeft;
        int y = guiTop;
        enableItemPullButton = add(new GuiButtonCheck(TilePipeBelt.BUTTON_ITEM_PULL, x + 10, y + 45, 1, host.pullItems));
        enableItemEjectButton = add(new GuiButtonCheck(TilePipeBelt.BUTTON_ITEM_EJECT, x + 10, y + 57, 1, host.shouldEjectItems));
        renderTopButton = add(new GuiButtonCheck(TilePipeBelt.BUTTON_RENDER_TOP, x + 10, y + 69, 1, !host.renderTop));
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        enableItemPullButton.setChecked(host.pullItems);
        renderTopButton.setChecked(!host.renderTop);
        enableItemEjectButton.setChecked(host.shouldEjectItems);
    }

    @Override
    protected void actionPerformed(GuiButton button)
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

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        final String tileName = "tile." + AssemblyLine.PREFIX + "belt.pipe.gui";
        drawStringCentered(LanguageUtility.getLocal(tileName), xSize / 2, 5);

        drawString(LanguageUtility.getLocal(tileName + ".button.item.pull"), 23, 46);
        drawString(LanguageUtility.getLocal(tileName + ".button.item.eject"), 23, 58);
        drawString(LanguageUtility.getLocal(tileName + ".button.render"), 23, 70);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        drawContainerSlots();
    }
}
