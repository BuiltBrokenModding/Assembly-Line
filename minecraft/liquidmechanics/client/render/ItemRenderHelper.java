package liquidmechanics.client.render;

import liquidmechanics.client.model.ModelGearRod;
import liquidmechanics.client.model.ModelGenerator;
import liquidmechanics.client.model.ModelLargePipe;
import liquidmechanics.client.model.ModelLiquidTank;
import liquidmechanics.client.model.ModelPump;
import liquidmechanics.client.model.ModelReleaseValve;
import liquidmechanics.common.LiquidMechanics;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

/** special tanks to Mekanism github */
public class ItemRenderHelper implements IItemRenderer
{
    private ModelPump modelPump = new ModelPump();
    private ModelGearRod modelRod = new ModelGearRod();
    private ModelGenerator modelGen = new ModelGenerator();
    private ModelLargePipe SixPipe = new ModelLargePipe();
    private ModelLiquidTank tank = new ModelLiquidTank();
    private ModelReleaseValve valve = new ModelReleaseValve();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if (item.itemID == LiquidMechanics.blockPipe.blockID)
        {
            this.renderPipeItem((RenderBlocks) data[0], item.getItemDamage(), type == ItemRenderType.EQUIPPED);
        }
        if (item.itemID == LiquidMechanics.blockReleaseValve.blockID)
        {
            this.renderReleaseValve((RenderBlocks) data[0], item.getItemDamage(), type == ItemRenderType.EQUIPPED);
        }

    }

    public void renderPipeItem(RenderBlocks renderer, int meta, boolean equ)
    {

        GL11.glPushMatrix();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(RenderPipe.getPipeTexture(meta)));

        if (!equ)
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            SixPipe.renderRight();
            SixPipe.renderLeft();
            SixPipe.renderMiddle();
        }
        else
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            SixPipe.renderFront();
            SixPipe.renderBack();
            SixPipe.renderMiddle();
        }

        GL11.glPopMatrix();
    }

    public void renderReleaseValve(RenderBlocks renderer, int meta, boolean equ)
    {
        GL11.glPushMatrix();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(RenderPipe.getPipeTexture(15)));
        if (!equ)
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            SixPipe.renderRight();
            SixPipe.renderLeft();
            SixPipe.renderMiddle();
        }
        else
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            SixPipe.renderFront();
            SixPipe.renderBack();
            SixPipe.renderMiddle();
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(LiquidMechanics.RESOURCE_PATH + "ReleaseValve.png"));
        GL11.glRotatef(180f, 0f, 0f, 1f);
        if (!equ)
        {
           GL11.glTranslatef(0, -2.0F, 0);
        }
        else
        {
            GL11.glTranslatef(0, -2.0F, 0);
        }
        valve.render();
        GL11.glPopMatrix();
    }

}
