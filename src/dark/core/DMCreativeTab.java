package dark.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DMCreativeTab extends CreativeTabs
{
    public ItemStack itemStack = new ItemStack(Item.ingotIron, 1, 0);

    private static DMCreativeTab tabAutomation = new DMCreativeTab("Automation");
    private static DMCreativeTab tabIndustrial = new DMCreativeTab("Industrial");
    private static DMCreativeTab tabHydrualic = new DMCreativeTab("Hydraulic");
    private static DMCreativeTab tabMining = new DMCreativeTab("Mining");

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

    public static DMCreativeTab tabAutomation()
    {
        if (tabAutomation == null)
        {
            tabAutomation = new DMCreativeTab("Automation");
        }
        return tabAutomation;
    }

    public static DMCreativeTab tabIndustrial()
    {
        if (tabIndustrial == null)
        {
            tabIndustrial = new DMCreativeTab("Industrial");
        }
        return tabIndustrial;
    }

    public static DMCreativeTab tabHydraulic()
    {
        if (tabHydrualic == null)
        {
            tabHydrualic = new DMCreativeTab("Hydraulic");
        }
        return tabHydrualic;
    }

    public static DMCreativeTab tabMining()
    {
        if (tabMining == null)
        {
            tabMining = new DMCreativeTab("Mining");
        }
        return tabMining;
    }

}
