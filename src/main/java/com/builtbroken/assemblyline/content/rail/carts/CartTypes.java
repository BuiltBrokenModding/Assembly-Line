package com.builtbroken.assemblyline.content.rail.carts;

import com.builtbroken.mc.prefab.entity.cart.EntityAbstractCart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

/**
 * List of cart types that are supported by {@link EntityAbstractCart}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/21/2016.
 */
public enum CartTypes
{
    /** Cart with nothing */
    EMPTY(null, 0.7f, 0.7f, 0),
    /** Cart with chest */
    CHEST("chest", 0.7f, 0.7f, 27),
    /** Cart with single slot that can take large stacks or blocks placed onto it. Visually rendering said blocks */
    STACK("stack", 0.7f, 0.7f, 1),
    /** Assembly Line Crate */
    CRATE("crate", 0.7f, 0.7f, 15),
    /** JABBA https://mods.curse.com/mc-mods/minecraft/jabba */
    JABBA_BARREL("jabba", 0.7f, 0.7f, 16);

    //Entity Data
    public final float width;
    public final float length;

    //Cart data
    public int inventorySize;

    //Client data
    @SideOnly(Side.CLIENT)
    public IIcon icon;
    public final String subName;

    CartTypes(String name, float width, float length, int slots)
    {
        this.width = width;
        this.length = length;
        this.inventorySize = slots;
        this.subName = name;
    }

    public static CartTypes get(int itemDamage)
    {
        if (itemDamage > 0 && itemDamage < values().length)
        {
            return values()[itemDamage];
        }
        return EMPTY;
    }
}
