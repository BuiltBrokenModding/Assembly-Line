package dark.common.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import universalelectricity.core.item.ItemElectric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.common.DarkMain;

public class ItemBattery extends ItemElectric
{
    public ItemBattery(String name, int id)
    {
        super(DarkMain.CONFIGURATION.getItem(name, id).getInt(id));
        this.setUnlocalizedName(DarkMain.getInstance().PREFIX + name);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return "item." + this.getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon(this.getUnlocalizedName().replace("item.", ""));
    }

    @Override
    public float getVoltage(ItemStack itemStack)
    {
        return 25;
    }

    @Override
    public float getMaxElectricityStored(ItemStack theItem)
    {
        return 1000000;
    }
}
