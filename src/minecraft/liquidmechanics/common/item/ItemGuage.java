package liquidmechanics.common.item;

import java.util.List;

import liquidmechanics.api.IReadOut;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.TabLiquidMechanics;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemGuage extends Item
{
    private int spawnID;

    public ItemGuage(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setIconIndex(10);
        this.setItemName("lmTool");
        this.setCreativeTab(TabLiquidMechanics.INSTANCE);
        this.setMaxStackSize(1);
        this.setTextureFile(LiquidMechanics.ITEM_TEXTURE_FILE);
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(this, 1, 0));
    }

    @Override
    public int getIconFromDamage(int par1)
    {
        switch (par1)
        {
            case 0:
                return 24;
            case 1:
        }
        return this.iconIndex;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World par3World, int x, int y, int z, int side, float par8, float par9, float par10)
    {
        if (!par3World.isRemote)
        {
            int meta = itemStack.getItemDamage();
            TileEntity blockEntity = par3World.getBlockTileEntity(x, y, z);

            // pipe Guage
            if (meta == 0)
            {

                if (blockEntity instanceof IReadOut)
                {
                    String output = ((IReadOut) blockEntity).getMeterReading(player, ForgeDirection.getOrientation(side));
                    if (output.length() > 100)
                    {
                        output = output.substring(0, 100);
                    }
                    output.trim();
                    player.sendChatToPlayer("ReadOut: " + output);
                }
            } else if (meta == 1)
            {

            }

        }

        return false;
    }

    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        return this.getItemName() + "." + itemstack.getItemDamage();
    }
}
