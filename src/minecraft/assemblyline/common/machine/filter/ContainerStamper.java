package assemblyline.common.machine.filter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ContainerStamper extends Container
{
	private World worldObj;
	private Vector3 position;

	public ContainerStamper(InventoryPlayer par1InventoryPlayer, World worldObj, Vector3 position)
	{
		this.worldObj = worldObj;
		this.position = position;

		// Paper Input
		this.addSlotToContainer(new Slot(par1InventoryPlayer, 0, 33, 34));
		// Item Stamp
		this.addSlotToContainer(new Slot(par1InventoryPlayer, 1, 33 + 18, 34));
		// Output Filter
		this.addSlotToContainer(new Slot(par1InventoryPlayer, 2, 33 + 36, 34));

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
	public void updateCraftingResults()
	{
		super.updateCraftingResults();
		System.out.println("WORK");
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		return true;
	}

	/**
	 * Called to transfer a stack from one inventory to the other eg. when shift clicking.
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
