package dark.farmtech.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.farmtech.FarmTech;

/** Bucket containing compost for farming
 *
 * @author DarkGuardsman */
public class ItemBucketCompost extends Item
{
    public ItemBucketCompost(int itemID)
    {
        super(FarmTech.CONFIGURATION.getItem("Bucket", itemID).getInt());
        this.setCreativeTab(FarmTech.TabFarmTech);
        this.setHasSubtypes(true);
        this.setContainerItem(Item.bucketEmpty);
        this.setUnlocalizedName("farmBucket");
        this.func_111206_d(FarmTech.instance.PREFIX+"farmBucket");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(FarmTech.instance.PREFIX + "compostBucket");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int par1)
    {
        return this.itemIcon;
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(this.itemID, 1, 0));
    }

}
