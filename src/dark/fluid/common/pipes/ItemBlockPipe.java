package dark.fluid.common.pipes;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dark.api.ColorCode.IColorCoded;
import dark.fluid.common.FMRecipeLoader;

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
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return Block.blocksList[this.getBlockID()].getUnlocalizedName() + "." + itemStack.getItemDamage();
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
        {
            TileEntity tile = world.getBlockTileEntity(x, y, z);
            if (tile instanceof IColorCoded)
            {

                if (tile instanceof TileEntityPipe)
                {
                    ((TileEntityPipe) tile).setPipeID(stack.getItemDamage());
                }
                else
                {
                    ((IColorCoded) tile).setColor((stack.getItemDamage() % 16) & 15);
                }
            }
            return true;
        }
        return false;
    }
}
