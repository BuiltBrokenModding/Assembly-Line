package fluidmech.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fluidmech.common.FluidMech;
import fluidmech.common.TabFluidMech;

public class ItemBasic extends Item
{
	protected List<Icon> icons = new ArrayList<Icon>();

	public ItemBasic(String name, int id)
	{
		super(id);
		this.setUnlocalizedName(name);
		this.setCreativeTab(TabFluidMech.INSTANCE);
	}

	@SideOnly(Side.CLIENT)
	public void func_94581_a(IconRegister iconRegister)
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		this.getSubItems(this.itemID, this.getCreativeTab(), list);

		if (list.size() > 0)
		{
			for (ItemStack itemStack : list)
			{
				this.icons.add(iconRegister.registerIcon(this.getUnlocalizedName(itemStack).replace("item.", FluidMech.TEXTURE_NAME_PREFIX)));
			}
		}
		else
		{
			this.iconIndex = iconRegister.registerIcon(this.getUnlocalizedName().replace("item.", FluidMech.TEXTURE_NAME_PREFIX));
		}
	}

	@Override
	public Icon getIconFromDamage(int damage)
	{
		if (this.icons.size() > damage)
		{
			return icons.get(damage);
		}

		return super.getIconFromDamage(damage);
	}
}