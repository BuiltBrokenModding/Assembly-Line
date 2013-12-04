package dark.machines.common.machines;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Item version of the enrgy storage block
 *
 * @author Darkguardsman */
public class ItemBlockEnergyStorage extends ItemBlock
{
    public ItemBlockEnergyStorage(int id)
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

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (!world.setBlock(x, y, z, this.getBlockID(), side, 3))
        {
            return false;
        }

        if (world.getBlockId(x, y, z) == this.getBlockID())
        {
            Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, player, stack);
            Block.blocksList[this.getBlockID()].onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }
}
