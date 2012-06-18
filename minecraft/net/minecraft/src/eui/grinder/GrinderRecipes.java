package net.minecraft.src.eui.grinder;
import net.minecraft.src.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GrinderRecipes
{
    private static final GrinderRecipes smeltingBase = new GrinderRecipes();

    /** The list of smelting results. */
    private Map smeltingList = new HashMap();
    private Map metaSmeltingList = new HashMap();

    /**
     * Used to call methods addSmelting and getSmeltingResult.
     */
    public static final GrinderRecipes smelting()
    {
        return smeltingBase;
    }

    private GrinderRecipes()
    {
        
        this.addSmelting(Item.coal.shiftedIndex, new ItemStack(mod_EUIndustry.coalNugget, 2,0));
        this.addSmelting(mod_EUIndustry.coalNugget.shiftedIndex,0, new ItemStack(mod_EUIndustry.coalNugget,2,1));
        this.addSmelting(mod_EUIndustry.coalNugget.shiftedIndex,1, new ItemStack(mod_EUIndustry.coalNugget,2,2));
    }

    /**
     * Adds a smelting recipe.
     */
    public void addSmelting(int par1, ItemStack par2ItemStack)
    {
        this.smeltingList.put(Integer.valueOf(par1), par2ItemStack);
    }

    public Map getSmeltingList()
    {
        return this.smeltingList;
    }
    
    /**
     * Add a metadata-sensitive furnace recipe
     * @param itemID The Item ID
     * @param metadata The Item Metadata
     * @param itemstack The ItemStack for the result
     */
    public void addSmelting(int itemID, int metadata, ItemStack itemstack)
    {
        metaSmeltingList.put(Arrays.asList(itemID, metadata), itemstack);
    }
    
    /**
     * Used to get the resulting ItemStack form a source ItemStack
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public ItemStack getSmeltingResult(ItemStack item) 
    {
        if (item == null)
        {
            return null;
        }
        ItemStack ret = (ItemStack)metaSmeltingList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (ret != null) 
        {
            return ret;
        }
        return (ItemStack)smeltingList.get(Integer.valueOf(item.itemID));
    }
}
