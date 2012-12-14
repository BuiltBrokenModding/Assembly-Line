package assemblyline.common.machine;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import assemblyline.common.machine.BlockMulti.MachineType;

public class ItemBlockMulti extends ItemBlock
{
	public ItemBlockMulti(int par1)
	{
		super(par1);
		this.setHasSubtypes(true);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return MachineType.get(itemstack.getItemDamage()).name;
	}

	@Override
	public int getMetadata(int par1)
	{
		return MachineType.get(par1).metadata;
	}
}
