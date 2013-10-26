package dark.core.common.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import universalelectricity.core.item.ItemElectric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DarkMain;
import dark.core.prefab.ModPrefab;

/** Simple battery to store energy
 *
 * @author DarkGuardsman */
public class ItemBattery extends ItemElectric
{
    public ItemBattery()
    {
        super(DarkMain.CONFIGURATION.getItem("Battery", ModPrefab.getNextItemId()).getInt());
        this.setUnlocalizedName(DarkMain.getInstance().PREFIX + "Battery");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
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
        return 5000;
    }
}
