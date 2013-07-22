package dark.assembly.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TabAssemblyLine extends CreativeTabs
{
	public static final TabAssemblyLine INSTANCE = new TabAssemblyLine("assemblyline");
	public static ItemStack itemStack;

	public TabAssemblyLine(String par2Str)
	{
		super(CreativeTabs.getNextID(), par2Str);
	}

	@Override
	public ItemStack getIconItemStack()
	{
		return itemStack;
	}
}
