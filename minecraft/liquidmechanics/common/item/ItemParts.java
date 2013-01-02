package liquidmechanics.common.item;

import java.util.List;

import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.TabLiquidMechanics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * A metadata item containing parts of various machines in Liquid Mechanics Mod.
 * 
 * @author Rs
 * 
 */
public class ItemParts extends Item {
	public enum Parts {
		Bronze("Bronze Tube", 0), Iron("Iron Tube", 1), Obby("Obby Tube", 2), Nether(
				"Nether Tube", 3), Seal("Seal", 16), SlimeSeal("Slime Seal", 17), Tank(
				"Tank", 18), Valve("Valve", 19);
		public String name;
		public int itemIndex;

		private Parts(String name, int itemIndex) {
			this.name = name;
			this.itemIndex = itemIndex;
		}
	}

	public ItemParts(int par1) {
		super(par1);
		this.setItemName("Parts");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		this.setCreativeTab(TabLiquidMechanics.INSTANCE);
		this.setTextureFile(LiquidMechanics.ITEM_TEXTURE_FILE);
	}

	@Override
	public int getIconFromDamage(int par1) {
		if (par1 < Parts.values().length) {
			return Parts.values()[par1].itemIndex;
		}
		return par1;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		if (itemstack.getItemDamage() < Parts.values().length) {
			return Parts.values()[itemstack.getItemDamage()].name;
		}
		return "unkown";
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		for (int i = 0; i < Parts.values().length; i++) {
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getItemName() {
		return "parts";
	}
}
