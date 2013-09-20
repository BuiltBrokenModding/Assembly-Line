package dark.core.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DMCreativeTab extends CreativeTabs
{
    public ItemStack itemStack = new ItemStack(Item.ingotIron, 1, 0);

    public static DMCreativeTab tabAutomation = new DMCreativeTab("Automation");
    public static DMCreativeTab tabIndustrial = new DMCreativeTab("Industrial");
    public static DMCreativeTab tabHydrualic = new DMCreativeTab("Hydrualic");

    public DMCreativeTab(String label)
    {
        super(label);
    }

    @Override
    public ItemStack getIconItemStack()
    {
        return this.itemStack;
    }

    public void setIconItemStack(ItemStack stack)
    {
        this.itemStack = stack;
    }

}
