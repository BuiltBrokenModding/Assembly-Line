package dark.core.client.renders;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.models.ModelSmallFluidCan;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.DarkMain;

@SideOnly(Side.CLIENT)
public class ItemRenderFluidCan implements IItemRenderer
{
    public static final ModelSmallFluidCan CAN_MODEL = new ModelSmallFluidCan();

    public static final ResourceLocation CAN_TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, DarkMain.MODEL_DIRECTORY + "FluidCanA.png");

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
        if (CoreRecipeLoader.itemFluidCan != null && item.itemID == CoreRecipeLoader.itemFluidCan.itemID)
        {
            GL11.glPushMatrix();

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(CAN_TEXTURE);
            if (type != ItemRenderType.EQUIPPED)
            {
                GL11.glTranslatef(0.5F, -0.5F, 0.5F);

            }
            else
            {
                GL11.glTranslatef(0.5F, -0.5F, 0.5F);
            }
            CAN_MODEL.render(0.0625F);
            GL11.glPopMatrix();
        }

    }
}
