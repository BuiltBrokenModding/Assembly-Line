package fluidmech.client.render;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import fluidmech.client.model.ModelGearRod;
import fluidmech.client.model.ModelGenerator;
import fluidmech.client.model.ModelLargePipe;
import fluidmech.client.model.ModelLiquidTank;
import fluidmech.client.model.ModelReleaseValve;
import fluidmech.client.render.pipeextentions.RenderPipe;
import fluidmech.common.FluidMech;

/** special tanks to Mekanism github */
public class ItemRenderHelper implements IItemRenderer
{
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
        if (item.itemID == FluidMech.blockPipe.blockID)
        {
            this.renderPipeItem((RenderBlocks) data[0], item.getItemDamage(), type == ItemRenderType.EQUIPPED);
        }
        if (item.itemID == FluidMech.blockReleaseValve.blockID)
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

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(FluidMech.MODEL_TEXTURE_DIRECTORY + "ReleaseValve.png"));
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
