package dark.core.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import dark.core.common.DarkMain;

public class ItemBlockColored extends ItemBlock
{
    public ItemBlockColored(int par1)
    {
        super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return Block.blocksList[this.getBlockID()].getUnlocalizedName() + "." + DarkMain.dyeColorNames[par1ItemStack.getItemDamage()];
    }

    @Override
    public String getUnlocalizedName()
    {
        return Block.blocksList[this.getBlockID()].getUnlocalizedName();
    }
}
