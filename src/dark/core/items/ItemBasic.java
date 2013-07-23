package dark.core.items;

import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import dark.core.DarkMain;

public class ItemBasic extends Item
{
	public static final Icon[] ICONS = new Icon[256];

	public ItemBasic(int itemID, String name, Configuration config)
	{
		super(config.getItem(name, itemID).getInt());
		this.setUnlocalizedName(DarkMain.getInstance().PREFIX + name);
	}

}
