package fluidmech.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TabFluidMech extends CreativeTabs
{
	public static final TabFluidMech INSTANCE = new TabFluidMech("fluidMechanics");
	private static ItemStack itemStack;

	public TabFluidMech(String par2Str)
	{
		super(CreativeTabs.getNextID(), par2Str);
	}

	public static void setItemStack(ItemStack newItemStack)
	{
		if (itemStack == null)
		{
			itemStack = newItemStack;
		}
	}

	@Override
	public ItemStack getIconItemStack()
	{
		if (itemStack == null) { return new ItemStack(Block.blocksList[this.getTabIconItemIndex()]); }

		return itemStack;
	}
}
