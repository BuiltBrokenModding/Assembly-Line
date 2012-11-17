package assemblyline.machines.crafter;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

/**
 * I am planning to make the crafter not use a
 * GUI.
 * 
 * @author Calclavia
 * 
 */
@Deprecated
public class ContainerCrafter extends Container
{
	private TileEntityAutoCrafter tileEntity;

	public ContainerCrafter(InventoryPlayer par1InventoryPlayer, TileEntityAutoCrafter tileEntity)
	{
		this.tileEntity = tileEntity;
		for (int r = 0; r < 3; r++)
		{
			for (int i = 0; i < 3; i++)
			{
				// this.addSlotToContainer(new
				// Slot(tileEntity, i + r * 3, 33
				// + i * 18, 34 + r * 18));
			}
		}
		int var3;

		for (var3 = 0; var3 < 3; ++var3)
		{
			for (int var4 = 0; var4 < 9; ++var4)
			{
				this.addSlotToContainer(new Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
			}
		}

		for (var3 = 0; var3 < 9; ++var3)
		{
			this.addSlotToContainer(new Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		return true;
	}

	/**
	 * Called to transfer a stack from one
	 * inventory to the other eg. when shift
	 * clicking.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1)
	{
		ItemStack itemStack3 = null;
		Slot itemStack = (Slot) this.inventorySlots.get(par1);

		if (itemStack != null && itemStack.getHasStack())
		{
			ItemStack itemStack2 = itemStack.getStack();
			itemStack3 = itemStack2.copy();

			if (par1 != 0)
			{
				if (itemStack2.itemID == Item.coal.shiftedIndex)
				{
					if (!this.mergeItemStack(itemStack2, 0, 1, false)) { return null; }
				}
				else if (par1 >= 30 && par1 < 37 && !this.mergeItemStack(itemStack2, 3, 30, false)) { return null; }
			}
			else if (!this.mergeItemStack(itemStack2, 3, 37, false)) { return null; }

			if (itemStack2.stackSize == 0)
			{
				itemStack.putStack((ItemStack) null);
			}
			else
			{
				itemStack.onSlotChanged();
			}

			if (itemStack2.stackSize == itemStack3.stackSize) { return null; }

			itemStack.onPickupFromSlot(par1EntityPlayer, itemStack2);
		}

		return itemStack3;
	}
}
