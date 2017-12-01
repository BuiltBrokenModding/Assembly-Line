package com.builtbroken.assemblyline.content.belt.pipe.data;

import com.builtbroken.mc.api.IInventoryFilter;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/1/2017.
 */
public class BeltInventoryFilter implements IInventoryFilter
{
    @Override
    public boolean isStackInFilter(ItemStack stack)
    {
        return true;
    }
}
