package assemblyline.common.machine.filter;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import assemblyline.common.AssemblyLine;

public class SlotFilter extends Slot
{

	public SlotFilter(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
	}

	public boolean isItemValid(ItemStack itemStack)
	{
		return itemStack.itemID == AssemblyLine.itemFilter.shiftedIndex;
	}

}
