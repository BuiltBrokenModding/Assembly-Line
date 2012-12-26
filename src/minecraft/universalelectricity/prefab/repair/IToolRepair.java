package universalelectricity.prefab.repair;

import net.minecraft.item.ItemStack;

public interface IToolRepair
{
	/**
	 * A unique ID for mods to recognize what repair tool this is.
	 */
	public String getID();

	/**
	 * How effective is this repairing tool?
	 * 
	 * @param itemStack The ItemStack
	 * @return A effectiveness value.
	 */
	public int getEffectiveness(ItemStack itemStack);
}
