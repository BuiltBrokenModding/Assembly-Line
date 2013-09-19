package dark.fluid.client.render;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.prefab.ModPrefab;
import dark.fluid.client.model.ModelReleaseValve;
import dark.fluid.common.FMRecipeLoader;
import dark.fluid.common.FluidMech;

@SideOnly(Side.CLIENT)
public class ItemRenderHelper implements IItemRenderer
{
    private ModelReleaseValve valve = new ModelReleaseValve();
    private RenderPipe pipe = new RenderPipe();

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
        if (item.itemID == FMRecipeLoader.blockPipe.blockID)
        {
            this.renderPipeItem((RenderBlocks) data[0], item, type == ItemRenderType.EQUIPPED);
        }
        if (item.itemID == FMRecipeLoader.blockReleaseValve.blockID)
        {
            this.renderReleaseValve((RenderBlocks) data[0], item.getItemDamage(), type == ItemRenderType.EQUIPPED);
        }

    }

    public void renderPipeItem(RenderBlocks renderer, ItemStack item, boolean equ)
    {

        GL11.glPushMatrix();

        FMLClientHandler.instance().getClient().renderEngine.func_110577_a(pipe.getTexture(item.itemID, item.getItemDamage()));
        if (!equ)
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            pipe.SixPipe.renderRight();
            pipe.SixPipe.renderLeft();
            pipe.SixPipe.renderMiddle();
        }
        else
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            pipe.SixPipe.renderFront();
            pipe.SixPipe.renderBack();
            pipe.SixPipe.renderMiddle();
        }

        GL11.glPopMatrix();
    }

    public void renderReleaseValve(RenderBlocks renderer, int meta, boolean equ)
    {
        GL11.glPushMatrix();
        FMLClientHandler.instance().getClient().renderEngine.func_110577_a(pipe.getTexture(FMRecipeLoader.blockPipe.blockID, 15));
        if (!equ)
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            pipe.SixPipe.renderRight();
            pipe.SixPipe.renderLeft();
            pipe.SixPipe.renderMiddle();
        }
        else
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            pipe.SixPipe.renderFront();
            pipe.SixPipe.renderBack();
            pipe.SixPipe.renderMiddle();
        }
        FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "ReleaseValve.png"));
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
