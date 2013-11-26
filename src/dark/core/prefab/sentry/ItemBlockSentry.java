package dark.core.prefab.sentry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import dark.core.prefab.ItemBlockHolder;

/** Item block that is the unplaced sentry gun. All sentry gun data is held by NBT to allow the
 * sentry gun to exist without metadata limits.
 * 
 * @author DarkGuardsman */
public class ItemBlockSentry extends ItemBlockHolder
{
    public ItemBlockSentry(int id)
    {
        super(id);
    }

    @Override
    public int getMetadata(int damage)
    {
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return Block.blocksList[this.getBlockID()].getUnlocalizedName() + "." + itemStack.getItemDamage();
    }

}
