package dark.assembly.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.CommonProxy;
import dark.assembly.common.machine.encoder.TileEntityEncoder;
import dark.core.prefab.invgui.GuiBase;
import dark.core.prefab.invgui.GuiButtonImage;

/** A base class for all ICBM Sentry GUIs.
 * 
 * @author Calclavia */
@SideOnly(Side.CLIENT)
public abstract class GuiEncoderBase extends GuiBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "gui_base.png");

    protected static final int MAX_BUTTON_ID = 3;
    protected TileEntityEncoder tileEntity;
    protected EntityPlayer entityPlayer;

    public GuiEncoderBase(EntityPlayer player, TileEntityEncoder tileEntity)
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
        // Inventory
        this.buttonList.add(new GuiButtonImage(0, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 0, 3));
        // Coding
        this.buttonList.add(new GuiButtonImage(1, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 22, 0));
        // Help
        this.buttonList.add(new GuiButtonImage(2, (this.width - this.guiSize.intX()) / 2 - 22, (this.height - this.guiSize.intY()) / 2 + 44, 2));

    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
            case 0:
            {
                this.entityPlayer.openGui(AssemblyLine.instance, CommonProxy.GUI_ENCODER, this.tileEntity.worldObj, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
                break;
            }
            case 1:
            {
                this.entityPlayer.openGui(AssemblyLine.instance, CommonProxy.GUI_ENCODER_CODE, this.tileEntity.worldObj, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
                break;
            }
            case 2:
            {
                this.entityPlayer.openGui(AssemblyLine.instance, CommonProxy.GUI_ENCODER_HELP, this.tileEntity.worldObj, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
                break;
            }
        }
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawForegroundLayer(int x, int y, float var1)
    {
        this.fontRenderer.drawString("\u00a77" + "Encoder", (int) (this.guiSize.intX() / 2 - 7 * 2.5), 4, 4210752);
        /** Render Tool Tips */
        if (((GuiButtonImage) this.buttonList.get(0)).isIntersect(x, y))
        {
            this.drawTooltip(x - this.guiTopLeftCorner.intX(), y - this.guiTopLeftCorner.intY() + 10, "Inventory");
        }
        else if (((GuiButtonImage) this.buttonList.get(1)).isIntersect(x, y))
        {
            this.drawTooltip(x - this.guiTopLeftCorner.intX(), y - this.guiTopLeftCorner.intY() + 10, "Coder");
        }
        else if (((GuiButtonImage) this.buttonList.get(2)).isIntersect(x, y))
        {
            this.drawTooltip(x - this.guiTopLeftCorner.intX(), y - this.guiTopLeftCorner.intY() + 10, "Help");
        }
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

    @Override
    public void drawTooltip(int x, int y, String... toolTips)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        if (toolTips != null)
        {
            int var5 = 0;
            int var6;
            int var7;

            for (var6 = 0; var6 < toolTips.length; ++var6)
            {
                var7 = this.fontRenderer.getStringWidth(toolTips[var6]);

                if (var7 > var5)
                {
                    var5 = var7;
                }
            }

            var6 = x + 12;
            var7 = y - 12;
            int var9 = 8;

            if (toolTips.length > 1)
            {
                var9 += 2 + (toolTips.length - 1) * 10;
            }

            if (this.guiTopLeftCorner.intY() + var7 + var9 + 6 > this.height)
            {
                var7 = this.height - var9 - this.guiTopLeftCorner.intY() - 6;
            }

            this.zLevel = 300.0F;
            int var10 = -267386864;
            this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
            this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
            int var11 = 1347420415;
            int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
            this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
            this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            for (int var13 = 0; var13 < toolTips.length; ++var13)
            {
                String var14 = "\u00a77" + toolTips[var13];

                this.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);

                if (var13 == 0)
                {
                    var7 += 2;
                }

                var7 += 10;
            }

            this.zLevel = 0.0F;
        }
    }
}
