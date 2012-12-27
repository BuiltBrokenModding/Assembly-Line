package assemblyline.common.machine.filter;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ContainerStamper extends Container implements IInventory
{
	private ItemStack[] containingItems = new ItemStack[3];
	private World worldObj;
	private Vector3 position;

	public ContainerStamper(InventoryPlayer par1InventoryPlayer, World worldObj, Vector3 position)
	{
		this.worldObj = worldObj;
		this.position = position;

		// Paper Input
		this.addSlotToContainer(new SlotFilter(this, 0, 30, 35));
		// Item Stamp
		this.addSlotToContainer(new Slot(this, 1, 66, 35));
		// Output Filter
		this.addSlotToContainer(new SlotFilterResult(this, 2, 124, 35));

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

		if (this.getStackInSlot(0) != null && this.getStackInSlot(1) != null)
		{
			if (this.getStackInSlot(0).getItem() instanceof ItemFilter)
			{
				ItemStack outputStack = this.getStackInSlot(0).copy();
				outputStack.stackSize = 1;
				ArrayList<ItemStack> filters = ItemFilter.getFilters(outputStack);

				for (ItemStack filteredStack : filters)
				{
					if (filteredStack.isItemEqual(this.getStackInSlot(1)))
					{
						this.setInventorySlotContents(2, null);
						return;
					}
				}

				filters.add(this.getStackInSlot(1));
				ItemFilter.setFilters(outputStack, filters);
				this.setInventorySlotContents(2, outputStack);
				return;
			}
		}

		this.setInventorySlotContents(2, null);

	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return this.isUseableByPlayer(player);
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

			if (par1 > 2)
			{
				if (this.getSlot(0).isItemValid(itemStack2))
				{
					if (!this.mergeItemStack(itemStack2, 0, 1, false)) { return null; }
				}
				else if (!this.mergeItemStack(itemStack2, 1, 2, false)) { return null; }
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

	@Override
	public int getSizeInventory()
	{
		return this.containingItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.containingItems[par1];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and
	 * returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var3 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var3;
		}
		else
		{
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as
	 * an EntityItem - like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.containingItems[par1] != null && par1 != 2)
		{
			ItemStack var2 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor
	 * sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;
	}

	@Override
	public String getInvName()
	{
		return "Stamper";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void onInventoryChanged()
	{

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1)
	{
		return true;
	}

	@Override
	public void openChest()
	{
	}

	@Override
	public void closeChest()
	{
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
	{
		super.onCraftGuiClosed(par1EntityPlayer);

		if (!this.worldObj.isRemote)
		{
			for (int i = 0; i < this.getSizeInventory(); ++i)
			{
				ItemStack itemStack = this.getStackInSlotOnClosing(i);

				if (itemStack != null)
				{
					par1EntityPlayer.dropPlayerItem(itemStack);
				}
			}
		}
	}
}
