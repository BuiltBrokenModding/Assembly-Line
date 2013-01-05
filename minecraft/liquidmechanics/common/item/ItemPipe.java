package liquidmechanics.common.item;

import java.util.List;

import liquidmechanics.api.helpers.Colors;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.TabLiquidMechanics;
import liquidmechanics.common.handlers.LiquidHandler;
import liquidmechanics.common.tileentity.TileEntityPipe;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemPipe extends Item
{
    int index = 32;
    private int spawnID;

    public ItemPipe(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setIconIndex(10);
        this.setItemName("itemPipe");
        this.setCreativeTab(TabLiquidMechanics.INSTANCE);
    }

    @Override
    public int getIconFromDamage(int par1)
    {

        return par1 + index;
    }

    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        return "pipe";
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < LiquidHandler.allowedLiquids.size() - 1; i++)
        {
            par3List.add(new ItemStack(this, 1, i));
        }
    }

    public String getTextureFile()
    {
        return LiquidMechanics.ITEM_TEXTURE_FILE;
    }

    @Override
    public String getItemName()
    {
        return "Pipes";
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        int blockID = par3World.getBlockId(x, y, z);
        spawnID = LiquidMechanics.blockPipe.blockID;
        if (blockID == Block.snow.blockID)
        {
            par7 = 1;
        }
        else if (blockID != Block.vine.blockID && blockID != Block.tallGrass.blockID && blockID != Block.deadBush.blockID)
        {
            if (par7 == 0)
            {
                --y;
            }

            if (par7 == 1)
            {
                ++y;
            }

            if (par7 == 2)
            {
                --z;
            }

            if (par7 == 3)
            {
                ++z;
            }

            if (par7 == 4)
            {
                --x;
            }

            if (par7 == 5)
            {
                ++x;
            }
        }

        if (LiquidMechanics.blockPipe.canPlaceBlockAt(par3World, x, y, z))
        {
            Block var9 = Block.blocksList[this.spawnID];
            par3World.editingBlocks = true;
            if (par3World.setBlockWithNotify(x, y, z, var9.blockID))
            {
                if (par3World.getBlockId(x, y, z) == var9.blockID)
                {

                    Block.blocksList[this.spawnID].onBlockAdded(par3World, x, y, z);
                    Block.blocksList[this.spawnID].onBlockPlacedBy(par3World, x, y, z, player);
                    TileEntity blockEntity = par3World.getBlockTileEntity(x, y, z);
                    if (blockEntity instanceof TileEntityPipe)
                    {
                        TileEntityPipe pipeEntity = (TileEntityPipe) blockEntity;
                        par3World.setBlockMetadataWithNotify(x,y,z,Colors.NONE.ordinal());
                    }
                }

                --itemstack.stackSize;
                par3World.editingBlocks = false;
                return true;
            }
        }
        par3World.editingBlocks = false;
        return false;
    }

}