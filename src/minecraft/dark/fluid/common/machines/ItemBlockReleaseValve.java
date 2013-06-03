package dark.fluid.common.machines;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReleaseValve extends ItemBlock
{

    public ItemBlockReleaseValve(int id)
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
		return Block.blocksList[this.getBlockID()].getUnlocalizedName() + "." + itemStack.getItemDamage();
	}
}
