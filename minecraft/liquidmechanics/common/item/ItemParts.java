package liquidmechanics.common.item;

import java.util.List;

import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.TabLiquidMechanics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** A metadata item containing parts of various machines in Liquid Mechanics Mod.
 * 
 * @author Rs */
public class ItemParts extends Item
{
    public enum Parts
    {
        Bronze("Bronze", 0),
        Iron("Iron", 1),
        Obby("Obby", 2),
        Nether("Nether", 3),
        Seal("Seal", 16),
        SlimeSeal("Slime", 17),
        Tank("Tank", 18),
        Valve("Valve", 19);

        public String name;
        public int itemIndex;

        private Parts(String name, int itemIndex)
        {
            this.name = name;
            this.itemIndex = itemIndex;
        }
    }

    public ItemParts(int par1)
    {
        super(par1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        this.setItemName("lmPart");
        this.setCreativeTab(TabLiquidMechanics.INSTANCE);
    }

    @Override
    public int getIconFromDamage(int par1)
    {
        if (par1 < Parts.values().length) { return Parts.values()[par1].itemIndex; }
        return par1;
    }

    @Override
    public String getTextureFile()
    {
        return LiquidMechanics.ITEM_TEXTURE_FILE;
    }

    @Override
    public String getItemNameIS(ItemStack i)
    {
        int j = i.getItemDamage();
        return i.getItem().getItemName() + "." + j;
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < Parts.values().length; i++)
        {
            par3List.add(new ItemStack(this, 1, i));
        }
    }
}
