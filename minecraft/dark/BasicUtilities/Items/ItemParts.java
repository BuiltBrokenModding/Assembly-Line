package dark.BasicUtilities.Items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dark.BasicUtilities.BasicUtilitiesMain;

public class ItemParts extends Item
{
    public enum basicParts
    {
        Bronze("Bronze Tube", 0), Iron("Iron Tube", 1), Obby("Obby Tube", 2),
        Nether("Nether Tube", 3), Seal("Seal", 16), SlimeSeal("Slime Seal", 17),
        Tank("unfinished Tank", 18), Valve("Valve", 19);
        public String name;
        public int itemIndex;

        private basicParts(String name, int itemIndex)
        {
            this.name = name;
            this.itemIndex = itemIndex;
        }
    }

    public ItemParts(int par1)
    {
        super(par1);
        this.setItemName("Parts");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public int getIconFromDamage(int par1)
    {
        if (par1 < basicParts.values().length) { return basicParts.values()[par1].itemIndex; }
        return par1;
    }

    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        if (itemstack.getItemDamage() < basicParts.values().length) { return basicParts.values()[itemstack.getItemDamage()].name; }
        return "unkown";
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < basicParts.values().length; i++)
        {
            par3List.add(new ItemStack(this, 1, i));
        }
    }

    public String getTextureFile()
    {
        return BasicUtilitiesMain.ITEM_PNG;
    }

    @Override
    public String getItemName()
    {
        return "parts";
    }
}
