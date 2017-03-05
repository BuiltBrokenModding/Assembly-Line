package com.builtbroken.assemblyline.content.rail.carts;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.prefab.entity.cart.EntityAbstractCart;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/31/2016.
 */
public class RenderCart extends Render
{
    private static final ResourceLocation trappedChestTexture = new ResourceLocation("textures/entity/chest/trapped.png");
    private static final ResourceLocation xmasChestTexture = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation chestTexture = new ResourceLocation("textures/entity/chest/normal.png");

    private ModelChest chestModel;

    public RenderCart()
    {
        this.shadowSize = 0.0F;
        chestModel = new ModelChest();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return SharedAssets.GREY_TEXTURE;
    }

    @Override
    public void doRender(final Entity entity, final double xx, final double yy, final double zz, final float p_76986_8_, final float delta)
    {
        final EntityCart cart = (EntityCart) entity;
        float f5 = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * delta;

        double x2 = MathHelper.lerp(cart.lastRenderX, xx, delta);
        double y2 = MathHelper.lerp(cart.lastRenderY, yy, delta);
        double z2 = MathHelper.lerp(cart.lastRenderZ, zz, delta);

        //Start all
        GL11.glPushMatrix();
        GL11.glTranslated(x2, y2, z2);
        GL11.glRotatef(180.0F - delta, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-f5, 0.0F, 0.0F, 1.0F);

        //store last position
        cart.lastRenderX = x2;
        cart.lastRenderY = y2;
        cart.lastRenderZ = z2;


        float halfWidth = cart.width / 2.0F;
        float halfLength = cart.length / 2.0F;
        float yaw = (float) Math.abs(MathUtility.clampAngleTo180(cart.rotationYaw));
        if (yaw >= 45 && yaw <= 135)
        {
            halfWidth = cart.length / 2.0F;
            halfLength = cart.width / 2.0F;
        }
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(
                -(double) halfWidth,
                0,
                -(double) halfLength,

                +(double) halfWidth,
                0.2,
                +(double) halfLength);

        //Render bottom of cart
        GL11.glPushMatrix();
        RenderUtility.renderCube(bounds, Blocks.iron_block);
        GL11.glPopMatrix();

        //Render stuff on top of cart
        if (cart.getType() != CartTypes.EMPTY)
        {
            GL11.glPushMatrix();
            if (cart.getType() == CartTypes.CHEST)
            {
                if (Engine.XMAS)
                {
                    this.bindTexture(xmasChestTexture);
                }
                else
                {
                    this.bindTexture(chestTexture);
                }
                GL11.glRotatef(180, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(0.6f, 0.6f, 0.6f);
                GL11.glTranslated(-0.5, -1.2, -0.5);
                chestModel.renderAll();
            }
            else if (cart.getType() == CartTypes.STACK)
            {
                ItemStack stack = cart.getInventory().getStackInSlot(0);
                try
                {
                    if (stack != null)
                    {
                        //TODO implement a custom override for rendering items on cart using an interface as well event
                        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.ENTITY);
                        if (renderer != null)
                        {
                            /** see if {@link net.minecraftforge.client.ForgeHooksClient#renderEntityItem(EntityItem, ItemStack, float, float, Random, TextureManager, RenderBlocks, int)}
                             *                  will work much better */
                            EntityItem fakeItem = new EntityItem(cart.world());
                            fakeItem.setPosition(cart.x(), cart.y() + 0.5, cart.z());
                            renderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, stack, RenderBlocks.getInstance(), fakeItem);

                        }
                        else if (stack.getItem() instanceof ItemBlock)
                        {
                            RenderUtility.renderCube(AxisAlignedBB.getBoundingBox(0, 0, 0, .8, .8, .8), Blocks.planks);
                        }
                        else
                        {
                            RenderItemOverlayUtility.renderIcon3D(stack.getItem().getIconFromDamage(stack.getItemDamage()), 1);
                        }
                    }
                }
                catch (Exception e)
                {
                    AssemblyLine.INSTANCE.logger().error("Failed to render " + stack, e);
                    RenderUtility.renderCube(AxisAlignedBB.getBoundingBox(0, 0, 0, .2, .1, .2), Blocks.wool);
                }
                RenderUtility.renderCube(AxisAlignedBB.getBoundingBox(0, 0, 0, .9, .1, .9), Blocks.planks);
            }
            else
            {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glTranslated(-0.5, .3, -0.5);
                RenderUtility.renderCube(AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1), Blocks.planks);
            }
            GL11.glPopMatrix();
        }

        //End all
        GL11.glPopMatrix();
        if (Engine.runningAsDev)
        {
            drawBounds(cart, xx, yy, zz);
        }
    }

    /**
     * Renders the bounding box around the cart
     *
     * @param cart
     * @param xx
     * @param yy
     * @param zz
     */
    protected void drawBounds(EntityAbstractCart cart, double xx, double yy, double zz)
    {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);

        float halfWidth = cart.width / 2.0F;
        float halfLength = cart.length / 2.0F;
        float yaw = (float) Math.abs(MathUtility.clampAngleTo180(cart.rotationYaw));
        if (yaw >= 45 && yaw <= 135)
        {
            halfWidth = cart.length / 2.0F;
            halfLength = cart.width / 2.0F;
        }

        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(xx - halfWidth, yy, zz - halfLength, xx + halfWidth, yy + (double) cart.height, zz + halfLength);
        RenderGlobal.drawOutlinedBoundingBox(axisalignedbb, 16777215);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }
}
