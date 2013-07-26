package dark.core.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import dark.core.DarkMain;

public class ItemOre extends ItemBlock
{

    public ItemOre(int par1)
    {
        super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return "tile." + DarkMain.getInstance().PREFIX + EnumMeterials.values()[par1ItemStack.getItemDamage()].name + "Ore";
    }

}
