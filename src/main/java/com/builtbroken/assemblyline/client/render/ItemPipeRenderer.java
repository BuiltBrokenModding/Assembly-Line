package com.builtbroken.assemblyline.client.render;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.builtbroken.assemblyline.ALRecipeLoader;
import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.client.model.ModelReleaseValve;
import com.builtbroken.assemblyline.fluid.pipes.FluidPartsMaterial;
import com.builtbroken.minecraft.DarkCore;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemPipeRenderer implements IItemRenderer
{
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
        if (item.itemID == ALRecipeLoader.blockPipe.blockID)
        {
            this.renderPipeItem((RenderBlocks) data[0], item, type == ItemRenderType.EQUIPPED);
        }
        if (item.itemID == ALRecipeLoader.blockReleaseValve.blockID)
        {
            this.renderReleaseValve((RenderBlocks) data[0], type == ItemRenderType.EQUIPPED);
        }

    }

    public void renderPipeItem(RenderBlocks renderer, ItemStack item, boolean equ)
    {
        GL11.glPushMatrix();
        GL11.glRotatef(180f, 0f, 0f, 1f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderPipe.getTexture(item.getItemDamage()));
        if (!equ)
        {
            GL11.glTranslatef(0F, -1F, 0F);
            RenderPipe.render(item.getItemDamage(), new boolean[] { false, false, false, false, true, true });
        }
        else
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            RenderPipe.render(item.getItemDamage(), new boolean[] { false, false, true, true, false, false });
        }
        GL11.glPopMatrix();
    }

    public void renderReleaseValve(RenderBlocks renderer, boolean equ)
    {
        GL11.glPushMatrix();
        GL11.glRotatef(180f, 0f, 0f, 1f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderPipe.getTexture(FluidPartsMaterial.STEEL, 0));
        if (!equ)
        {
            GL11.glTranslatef(0F, -1F, 0F);
            RenderPipe.render(FluidPartsMaterial.IRON, 0, new boolean[] { false, false, false, false, true, true });
        }
        else
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            RenderPipe.render(FluidPartsMaterial.IRON, 0, new boolean[] { false, false, true, true, false, false });
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(AssemblyLine.DOMAIN, DarkCore.MODEL_DIRECTORY + "ReleaseValve.png"));
        valve.render();
        GL11.glPopMatrix();
    }
}
