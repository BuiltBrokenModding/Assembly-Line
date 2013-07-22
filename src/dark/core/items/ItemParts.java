package dark.core.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

/**
 * Parts that are used for crafting higher up items and block. These parts have no use other that
 * crafting
 * 
 * @author DarkGuardsman
 * 
 */
public class ItemParts extends ItemBasic
{

	public enum Parts
	{
		VALVE("Valve"),
		SERVO("Servo");

		public String name;

		private Parts(String name)
		{
			this.name = name;
		}
	}

	public ItemParts(int itemID, Configuration config)
	{
		super(itemID, "lmPart",config);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		return "item." + Parts.values()[itemStack.getItemDamage()].name;
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < Parts.values().length; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}

}
