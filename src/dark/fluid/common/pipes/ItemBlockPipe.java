package dark.fluid.common.pipes;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import dark.fluid.common.FluidMech;

public class ItemBlockPipe extends ItemBlock
{

    public ItemBlockPipe(int id)
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
    public String getUnlocalizedName(ItemStack itemStack)
    {
        if (itemStack.itemID == FluidMech.recipeLoader.blockPipe.blockID)
        {
            return "tile.rpipe." + itemStack.getItemDamage();
        }
        return Block.blocksList[this.getBlockID()].getUnlocalizedName() + "." + itemStack.getItemDamage();
    }
}
