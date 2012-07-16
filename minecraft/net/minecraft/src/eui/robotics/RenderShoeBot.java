package net.minecraft.src.eui.robotics;

import static net.minecraft.src.forge.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraft.src.forge.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityBlaze;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.EnumAction;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelBlaze;
import net.minecraft.src.ModelGhast;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderLiving;
import net.minecraft.src.Tessellator;
import net.minecraft.src.forge.IItemRenderer;
import net.minecraft.src.forge.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

public class RenderShoeBot extends RenderLiving
{
    public RenderShoeBot()
    {
        super(new ModelModelShoeBot(), 0.5F);
    }

    protected void renderName(EntityShoeBot par1EntityGuard, double par2, double par4, double par6)
    {
        if (Minecraft.isGuiEnabled())
        {
            float var8 = 1.6F;
            float var9 = 0.016666668F * var8;
            float var10 = par1EntityGuard.getDistanceToEntity(this.renderManager.livingPlayer);
            float var11 = par1EntityGuard.isSneaking() ? 4.0F : 32.0F;

            if (var10 < var11)
            {
                String var12 = par1EntityGuard.getRenderedName();

                
                    FontRenderer var13 = this.getFontRendererFromRenderManager();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)par2 + 0.0F, (float)par4 + 2.3F, (float)par6);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-var9, -var9, var9);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glTranslatef(0.0F, 0.25F / var9, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    Tessellator var14 = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var14.startDrawingQuads();
                    int var15 = var13.getStringWidth(var12) / 2;
                    var14.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    var14.addVertex((double)(-var15 - 1), -1.0D, 0.0D);
                    var14.addVertex((double)(-var15 - 1), 8.0D, 0.0D);
                    var14.addVertex((double)(var15 + 1), 8.0D, 0.0D);
                    var14.addVertex((double)(var15 + 1), -1.0D, 0.0D);
                    var14.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glDepthMask(true);
                    var13.drawString(var12, -var13.getStringWidth(var12) / 2, 0, 553648127);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
            }
        }
    
    public void renderBot(EntityShoeBot par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
    	double var13 = par4 - (double)par1Entity.yOffset - 1.2;
        super.doRenderLiving(par1Entity, par2, var13, par6, par8, par9);
    }
    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderBot((EntityShoeBot)par1Entity, par2, par4, par6, par8, par9);
    }
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderBot((EntityShoeBot)par1EntityLiving, par2, par4, par6, par8, par9);
    }
    @Override
    protected void passSpecialRender(EntityLiving par1EntityLiving, double par2, double par4, double par6)
    {
        this.renderName((EntityShoeBot)par1EntityLiving, par2, par4, par6);        
    }

}