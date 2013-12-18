package dark.machines.items;

import com.dark.IExtraInfo.IExtraItemInfo;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.core.item.ItemElectric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.DMCreativeTab;
import dark.core.prefab.ModPrefab;
import dark.machines.DarkMain;

/** Simple battery to store energy
 * 
 * @author DarkGuardsman */
public class ItemBattery extends ItemElectric implements IExtraItemInfo
{
    public ItemBattery()
    {
        super(DarkMain.CONFIGURATION.getItem("Battery", ModPrefab.getNextItemId()).getInt());
        this.setUnlocalizedName(DarkMain.getInstance().PREFIX + "Battery");
        this.setCreativeTab(DMCreativeTab.tabIndustrial());
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

    @Override
    public boolean hasExtraConfigs()
    {
        return false;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadOreNames()
    {
        OreDictionary.registerOre("Battery", this);
    }
}
