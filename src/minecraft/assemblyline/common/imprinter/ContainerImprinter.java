package assemblyline.common.imprinter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import assemblyline.common.AssemblyLine;
import dark.library.inv.ISlotWatcher;
import dark.library.inv.SlotCraftingResult;
import dark.library.inv.SlotRestricted;
import dark.library.inv.WatchedSlot;

public class ContainerImprinter extends Container implements ISlotWatcher
{
	public InventoryPlayer inventoryPlayer;
	public TileEntityImprinter tileEntity;

	public ContainerImprinter(InventoryPlayer inventoryPlayer, TileEntityImprinter tileEntity)
	{
		this.tileEntity = tileEntity;
		this.tileEntity.container = this;
		this.inventoryPlayer = inventoryPlayer;

		/**
		 * Crafting Matrix
		 */
		for (int x = 0; x < 3; x++)
		{
			for (int y = 0; y < 3; y++)
			{
				this.addSlotToContainer(new WatchedSlot(this.tileEntity, y + x * 3, 9 + y * 18, 16 + x * 18, this));
			}
		}

		// Imprint Input for Imprinting
		this.addSlotToContainer(new SlotRestricted(this.tileEntity, TileEntityImprinter.IMPRINTER_MATRIX_START, 68, 34, new ItemStack(AssemblyLine.itemImprint)));
		// Item to be imprinted
		this.addSlotToContainer(new WatchedSlot(this.tileEntity, TileEntityImprinter.IMPRINTER_MATRIX_START + 1, 92, 34, this));
		// Result of Crafting/Imprinting
		this.addSlotToContainer(new SlotCraftingResult(this.tileEntity, this, this.tileEntity, TileEntityImprinter.IMPRINTER_MATRIX_START + 2, 148, 34));

		// Imprinter Inventory
		for (int ii = 0; ii < 2; ii++)
		{
			for (int i = 0; i < 9; i++)
			{
				this.addSlotToContainer(new WatchedSlot(this.tileEntity, (i + ii * 9) + this.tileEntity.INVENTORY_START, 8 + i * 18, 80 + ii * 18, this));
			}
		}

		// Player Inventory
		int var3;

		for (var3 = 0; var3 < 3; ++var3)
		{
			for (int var4 = 0; var4 < 9; ++var4)
			{
				this.addSlotToContainer(new WatchedSlot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 120 + var3 * 18, this));
			}
		}

		for (var3 = 0; var3 < 9; ++var3)
		{
			this.addSlotToContainer(new WatchedSlot(inventoryPlayer, var3, 8 + var3 * 18, 178, this));
		}

		this.tileEntity.openChest();
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
	{
		super.onCraftGuiClosed(par1EntityPlayer);
		this.tileEntity.closeChest();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return this.tileEntity.isUseableByPlayer(player);
	}

	/**
	 * Called to transfer a stack from one inventory to the other eg. when shift clicking.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack copyStack = null;
		Slot slotObj = (Slot) this.inventorySlots.get(slot);

		if (slotObj != null && slotObj.getHasStack())
		{
			ItemStack slotStack = slotObj.getStack();
			copyStack = slotStack.copy();

			if (slot == this.tileEntity.INVENTORY_START - 1)
			{
				// Prevents filter from being duplicated
				this.tileEntity.setInventorySlotContents(this.tileEntity.INVENTORY_START - 1, null);
			}

			if (slot > this.tileEntity.getSizeInventory() - 1)
			{
				if (this.getSlot(this.tileEntity.IMPRINTER_MATRIX_START).isItemValid(slotStack))
				{
					if (!this.mergeItemStack(slotStack, this.tileEntity.IMPRINTER_MATRIX_START, this.tileEntity.IMPRINTER_MATRIX_START + 1, true))
					{
						return null;
					}
				}
				else if (!this.mergeItemStack(slotStack, this.tileEntity.INVENTORY_START, this.tileEntity.getSizeInventory(), false))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(slotStack, this.tileEntity.getSizeInventory(), this.tileEntity.getSizeInventory() + 36, false))
			{
				return null;
			}

			if (slotStack.stackSize == 0)
			{
				slotObj.putStack(null);
			}
			else
			{
				slotObj.onSlotChanged();
			}

			if (slotStack.stackSize == copyStack.stackSize)
			{
				return null;
			}

			slotObj.onPickupFromSlot(player, slotStack);
		}

		this.slotContentsChanged(slot);
		return copyStack;
	}

	@Override
	public void slotContentsChanged(int slot)
	{
		this.tileEntity.onInventoryChanged();
		this.detectAndSendChanges();
	}
}
