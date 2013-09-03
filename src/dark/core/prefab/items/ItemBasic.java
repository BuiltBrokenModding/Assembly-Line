package dark.core.prefab.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DarkMain;

public class ItemBasic extends Item
{
    public static final Icon[] ICONS = new Icon[256];

    public ItemBasic(int itemID, String name, Configuration config)
    {
        super(config.getItem(name, itemID).getInt());
        this.setUnlocalizedName(name);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon(DarkMain.getInstance().PREFIX + this.getUnlocalizedName().replace("item.", ""));
    }
}
