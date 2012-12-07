package dark.BasicUtilities.Items;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IForce;
import dark.BasicUtilities.api.IReadOut;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.pipes.TileEntityPipe;
import dark.BasicUtilities.tanks.TileEntityLTank;

public class ItemGuage extends Item
{
    private int spawnID;

    public ItemGuage(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setIconIndex(10);
        this.setItemName("guage");
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setMaxStackSize(1);
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
        }
        return this.iconIndex;
    }

    public String getTextureFile()
    {
        return BasicUtilitiesMain.ITEM_PNG;
    }

    @Override
    public String getItemName()
    {
        return "guage";
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World par3World, int x, int y, int z, int side, float par8, float par9, float par10)
    {
        if (!par3World.isRemote)
        {
            if (itemStack.getItemDamage() == 0)
            {
                TileEntity blockEntity = par3World.getBlockTileEntity(x, y, z);
                if(blockEntity instanceof IReadOut)
                {
                    String output = ((IReadOut) blockEntity).getMeterReading(player,ForgeDirection.getOrientation(side));
                    if(output.length() > 100) output = output.substring(0, 100);
                    output.trim();
                    player.sendChatToPlayer("ReadOut: "+output);
                }
            }

        }

        return false;
    }

    public String getItemNameIS(ItemStack par1ItemStack)
    {
        int var3 = par1ItemStack.getItemDamage();
        switch (var3)
        {
            case 0:
                return "PipeGuage";
        }
        return this.getItemName();
    }
}
