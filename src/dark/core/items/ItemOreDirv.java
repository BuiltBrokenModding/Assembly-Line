package dark.core.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.DarkMain;

/** A series of items that are derived from a basic ore block
 *
 * @author DarkGuardsman */
public class ItemOreDirv extends ItemBasic
{

	/* EACH SUB IS ALLOCATED 20 metadata to store items in its group */

	public ItemOreDirv(int itemID, Configuration config)
	{
		super(itemID, "Metal_Parts", config);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{

		if (itemStack != null)
		{
			int meta = itemStack.getItemDamage();
			return "item." + DarkMain.getInstance().PREFIX + EnumOreParts.getFullName(meta);
		}
		else
		{
			return this.getUnlocalizedName();
		}
	}

	@Override
	public Icon getIconFromDamage(int i)
	{
		return ICONS[i];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		for (int j = 0; j < EnumOreParts.values().length; j++)
		{
			String suf = EnumOreParts.values()[j].name;
			int meta = EnumOreParts.values()[j].meta;
			for (int i = 0; i < EnumMeterials.values().length; i++)
			{
				ICONS[i + meta] = iconRegister.registerIcon(DarkMain.getInstance().PREFIX + EnumMeterials.values()[i].name + suf);
			}
		}
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int j = 0; j < EnumOreParts.values().length; j++)
		{
			String suf = EnumOreParts.values()[j].name;
			int i = EnumOreParts.values()[j].meta;
			for (; i < EnumMeterials.values().length + EnumOreParts.values()[j].meta; i++)
			{
				if (EnumMeterials.values()[i - EnumOreParts.values()[j].meta].shouldCreateItem(EnumOreParts.values()[j]))
				{
					par3List.add(new ItemStack(this, 1, i));
				}
			}
		}
	}

}
