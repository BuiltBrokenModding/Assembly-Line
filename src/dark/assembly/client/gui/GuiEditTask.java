package dark.assembly.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import dark.core.common.DarkMain;
import dark.core.prefab.invgui.GuiBase;
import dark.core.prefab.machine.TileEntityMachine;

public class GuiEditTask extends GuiBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, DarkMain.GUI_DIRECTORY + "gui_base_machine.png");

    int guiID = -1;
    protected TileEntityMachine tileEntity;
    protected EntityPlayer entityPlayer;
    protected Object mod;

    public GuiEditTask(Object mod, int returnGuiID, EntityPlayer player, TileEntityMachine tileEntity)
    {
        this.tileEntity = tileEntity;
        this.entityPlayer = player;
        this.guiSize.y = 380 / 2;
        this.mod = mod;
        this.guiID = returnGuiID;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(0, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 0, "Edit"));

        this.buttonList.add(new GuiButton(1, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 22, "Cancel"));

        this.buttonList.add(new GuiButton(2, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 44, "Del"));

    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
            case 0:
            {
                //TODO apply changes to task
                entityPlayer.openGui(mod, guiID, tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                break;
            }
            case 1:
            {

                entityPlayer.openGui(mod, guiID, tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                break;
            }
            case 2:
            {
                //TODO remove task from program
                entityPlayer.openGui(mod, guiID, tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                break;
            }
        }
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawForegroundLayer(int x, int y, float var1)
    {
        this.fontRenderer.drawString("\u00a77" + tileEntity.getInvName(), (int) (this.guiSize.intX() / 2 - 7 * 2.5), 4, 4210752);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawBackgroundLayer(int x, int y, float var1)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int containerWidth = (this.width - this.guiSize.intX()) / 2;
        int containerHeight = (this.height - this.guiSize.intY()) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.guiSize.intX(), this.guiSize.intY());
    }

}
