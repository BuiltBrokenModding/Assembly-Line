package liquidmechanics.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class TabLiquidMechanics extends CreativeTabs
{
	public static final TabLiquidMechanics INSTANCE = new TabLiquidMechanics("BasicPipes");
	private static ItemStack itemStack;

	public TabLiquidMechanics(String par2Str)
	{
		super(CreativeTabs.getNextID(), par2Str);
		LanguageRegistry.instance().addStringLocalization("itemGroup.BasicPipes", "en_US", "Basic Pipes");
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
