package assemblyline.common;

import universalelectricity.prefab.UETab;
import net.minecraft.item.Item;

public class ItemFilter extends Item
{
	public ItemFilter(int id)
	{
		super(id);
		this.setIconIndex(Item.paper.getIconFromDamage(0));
		this.setCreativeTab(UETab.INSTANCE);
	}

}
