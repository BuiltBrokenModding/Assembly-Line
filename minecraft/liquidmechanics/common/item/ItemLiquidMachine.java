package liquidmechanics.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemLiquidMachine extends ItemBlock
{

    public ItemLiquidMachine(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getItemNameIS(ItemStack par1ItemStack)
    {
        return Block.blocksList[this.getBlockID()].getBlockName() + "." + (par1ItemStack.getItemDamage());
    }

    @Override
    public String getItemName()
    {
        return Block.blocksList[this.getBlockID()].getBlockName() + ".0";
    }
}
