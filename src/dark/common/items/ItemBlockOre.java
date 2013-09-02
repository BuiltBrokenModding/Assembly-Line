package dark.common.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import dark.common.DarkMain;

public class ItemBlockOre extends ItemBlock
{

    public ItemBlockOre(int par1)
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
