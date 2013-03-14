package fluidmech.common.item;

import hydraulic.core.implement.IReadOut;

import java.util.List;

import universalelectricity.components.common.BasicComponents;
import universalelectricity.components.common.item.ItemBasic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import fluidmech.common.FluidMech;
import fluidmech.common.TabFluidMech;

public class ItemGuage extends ItemBasic
{
    private int spawnID;

    public ItemGuage(int id)
    {
        super("lmTool", id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(TabFluidMech.INSTANCE);
        this.setMaxStackSize(1);
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(this, 1, 0));
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
}
