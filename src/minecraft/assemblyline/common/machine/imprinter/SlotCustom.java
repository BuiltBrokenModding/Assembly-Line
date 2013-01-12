package assemblyline.common.machine.imprinter;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import assemblyline.common.AssemblyLine;

public class SlotCustom extends Slot
{
	private ItemStack itemStack;

	public SlotCustom(IInventory par1iInventory, int par2, int par3, int par4, ItemStack itemStack)
	{
		super(par1iInventory, par2, par3, par4);
		this.itemStack = itemStack;
	}

	public boolean isItemValid(ItemStack itemStack)
	{
		return itemStack.isItemEqual(this.itemStack);
	}
}
