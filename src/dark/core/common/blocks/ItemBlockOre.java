package dark.core.common.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import dark.core.common.DarkMain;
import dark.core.common.blocks.BlockOre.OreData;
import dark.core.common.items.EnumMeterials;

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
        if (par1ItemStack != null && par1ItemStack.getItemDamage() < OreData.values().length)
        {
            return "tile." + DarkMain.getInstance().PREFIX + OreData.values()[par1ItemStack.getItemDamage()].name + "Ore";
        }
        return super.getUnlocalizedName();
    }

}
