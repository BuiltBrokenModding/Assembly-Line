package dark.BasicUtilities;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import dark.BasicUtilities.renders.models.ModelLargePipe;
import dark.BasicUtilities.renders.models.ModelLiquidTank;

/**
 * special tanks to Mekanism github
 */
public class ItemRenderHelper implements IItemRenderer
{
    static final ModelLiquidTank model = new ModelLiquidTank();
    static final ModelLargePipe SixPipe = new ModelLargePipe();

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
        if (item.itemID == BasicUtilitiesMain.itemPipes.shiftedIndex)
        {
            this.renderPipeItem((RenderBlocks) data[0], item.getItemDamage(), type == ItemRenderType.EQUIPPED);
        }
        if (item.itemID == BasicUtilitiesMain.itemTank.shiftedIndex)
        {
            this.rendertankItem((RenderBlocks) data[0], item.getItemDamage(), type == ItemRenderType.EQUIPPED);
        }

    }

    public void renderPipeItem(RenderBlocks renderer, int meta, boolean equ)
    {

        GL11.glPushMatrix();
        String file = BasicUtilitiesMain.textureFile + "pipes/";
        switch (meta)
        {
            case 0:
                file += "SixSteamPipe.png";
                break;
            case 1:
                file += "SixWaterPipe.png";
                break;
            case 2:
                file += "SixLavaPipe.png";
                break;
            case 3:
                file += "SixOilPipe.png";
                break;
            default:
                file += "DefaultPipe.png";
                break;
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(file));
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

    public void rendertankItem(RenderBlocks renderer, int meta, boolean equ)
    {

        GL11.glPushMatrix();

        String file = BasicUtilitiesMain.textureFile + "tanks/";
        switch (meta)
        {
            default:
                file += "LiquidTank.png";
                break;
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(file));
        if (!equ)
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
        }
        else
        {
            GL11.glTranslatef(0.5F, -0.5F, 0.5F);
        }
        model.renderMain(null, 0.0625F);
        GL11.glPopMatrix();
    }
}
