package dark.api.al;

import net.minecraft.item.ItemStack;

/** Applied to TileEntities that can accept a filter.
 * 
 * @author Calclavia */
public interface IFilterable
{
    public void setFilter(ItemStack filter);

    public ItemStack getFilter();
}
