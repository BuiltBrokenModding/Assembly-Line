package com.builtbroken.assemblyline.content.rail.carts;

import com.builtbroken.mc.prefab.entity.cart.EntityAbstractCart;

/**
 * List of cart types that are supported by {@link EntityAbstractCart}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/21/2016.
 */
public enum CartTypes
{
    /** Cart with nothing */
    EMPTY(0.7f, 0.7f, 0),
    /** Cart with chest */
    CHEST(0.7f, 0.7f, 27),
    /** Cart with single slot that can take large stacks or blocks placed onto it. Visually rendering said blocks */
    STACK(0.7f, 0.7f, 1),
    /** Assembly Line Crate */
    CRATE(0.7f, 0.7f, 15),
    /** JABBA https://mods.curse.com/mc-mods/minecraft/jabba  */
    JABBA_BARREL(0.7f, 0.7f, 16);

    public final float width;
    public final float length;
    public int inventorySize;

    CartTypes(float width, float length, int slots)
    {
        this.width = width;
        this.length = length;
        this.inventorySize = slots;
    }
}
