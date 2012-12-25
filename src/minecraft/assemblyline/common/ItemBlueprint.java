package assemblyline.common;

import universalelectricity.prefab.UETab;
import net.minecraft.item.Item;

public class ItemBlueprint extends Item
{
	public ItemBlueprint(int id)
	{
		super(id);
		this.setIconIndex(Item.paper.getIconFromDamage(0));
		this.setCreativeTab(UETab.INSTANCE);
	}

}
