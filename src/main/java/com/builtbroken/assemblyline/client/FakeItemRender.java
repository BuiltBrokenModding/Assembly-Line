package com.builtbroken.assemblyline.client;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/9/2018.
 */
public class FakeItemRender
{
    private static final RenderItem renderItem = new RenderItem()
    {
        @Override
        public boolean shouldBob()
        {
            return false;
        }
    };
    private static final EntityItem entityItem = new EntityItem(null);
    /**
     * Sets the world position of the fake item
     * <p>
     * Helps with special cases that use world data for rendering.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public static void setWorldPosition(World world, double x, double y, double z)
    {
        entityItem.worldObj = world;
        entityItem.posX = x;
        entityItem.posY = y;
        entityItem.posZ = z;
    }

    /**
     * Renders an item at the position
     * <p>
     * Make sure to call {@link #setWorldPosition(World, double, double, double)} first
     *
     * @param xx   - render position offset from center
     * @param yy   - render position offset from center
     * @param zz   - render position offset from center
     * @param item - item to render
     */
    public static void renderItemAtPosition(double xx, double yy, double zz, ItemStack item)
    {
        if (item != null)
        {
            //Start
            GL11.glPushMatrix();

            //Translate
            GL11.glTranslated(xx, yy, zz);
            final float scale = 1f;
            GL11.glScalef(scale, scale, scale);

            try
            {
                //Fix missing render manager
                renderItem.setRenderManager(RenderManager.instance);

                entityItem.setEntityItemStack(item);
                entityItem.hoverStart = 0.0F;

                //Render item using RenderItem class to save time
                renderItem.doRender(entityItem, 0, 0, 0, 0, 0);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            //End
            GL11.glPopMatrix();
        }
    }
}
