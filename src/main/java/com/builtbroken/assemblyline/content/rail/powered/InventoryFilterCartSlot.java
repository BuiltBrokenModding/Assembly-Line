package com.builtbroken.assemblyline.content.rail.powered;

import com.builtbroken.mc.api.rails.ITransportCartHasCargo;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.inventory.filters.IInventoryFilter;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/22/2016.
 */
public class InventoryFilterCartSlot implements IInventoryFilter
{
    public final ITransportCartHasCargo cart;
    public int slot = 0;

    public InventoryFilterCartSlot(ITransportCartHasCargo cart)
    {
        this.cart = cart;
    }

    @Override
    public boolean isStackInFilter(ItemStack stack)
    {
        if (cart.canAcceptItemForTransport(stack))
        {
            return cart.getInventory().getStackInSlot(slot) == null || InventoryUtility.stacksMatch(cart.getInventory().getStackInSlot(slot), stack);
        }
        return false;
    }
}
