package dark.BasicUtilities.pipes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.Liquid;

public class ItemEValve extends Item
{
    int index = 32;// 32 + 4 rows alloted to pipes
    private int spawnID;

    public ItemEValve(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setIconIndex(10);
        this.setItemName("eValve");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public int getIconFromDamage(int par1)
    {

        return par1 + index;
    }

    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        return itemstack.getItemDamage() < Liquid.values().length ? Liquid.getLiquid(itemstack.getItemDamage()).displayerName + " Pipe" : "Empty Pipe";
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < Liquid.values().length; i++)
        {
                par3List.add(new ItemStack(this, 1, i));
        }
    }

    public String getTextureFile()
    {
        return BasicUtilitiesMain.ITEM_PNG;
    }

    @Override
    public String getItemName()
    {
        return "Pipes";
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10)
    {
        int blockID = world.getBlockId(x, y, z);
        spawnID = BasicUtilitiesMain.eValve.blockID;
        int angle= MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (blockID == Block.snow.blockID)
        {
            side = 1;
        }
        else if (blockID != Block.vine.blockID && blockID != Block.tallGrass.blockID && blockID != Block.deadBush.blockID)
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        if (BasicUtilitiesMain.pipe.canPlaceBlockAt(world, x, y, z))
        {
            Block var9 = Block.blocksList[this.spawnID];
            world.editingBlocks = true;
            if (world.setBlockWithNotify(x, y, z, var9.blockID))
            {
                if (world.getBlockId(x, y, z) == var9.blockID)
                {

                    Block.blocksList[this.spawnID].onBlockAdded(world, x, y, z);
                    Block.blocksList[this.spawnID].onBlockPlacedBy(world, x, y, z, player);
                    TileEntity blockEntity = world.getBlockTileEntity(x, y, z);
                    if (blockEntity instanceof TileEntityEValve)
                    {
                        TileEntityEValve pipeEntity = (TileEntityEValve) blockEntity;
                        Liquid dm = Liquid.getLiquid(itemstack.getItemDamage());
                        pipeEntity.setType(dm);
                        pipeEntity.tank.setLiquid(Liquid.getStack(dm, 1));
                    }
                }

                --itemstack.stackSize;
                world.editingBlocks = false;
                return true;
            }
        }
        world.editingBlocks = false;
        return false;
    }

}