package assemblyline.common.machine.encoder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ContainerEncoder extends Container
{
	private ItemStack[] containingItems = new ItemStack[1];
	private World worldObj;
	private InventoryPlayer inventoryPlayer;
	private TileEntityEncoder encoder;

	public ContainerEncoder(InventoryPlayer inventoryPlayer, World worldObj, TileEntityEncoder encoder)
	{
		this.worldObj = worldObj;
		this.inventoryPlayer = inventoryPlayer;
		this.encoder = encoder;

		// Disk
		this.addSlotToContainer(new Slot(encoder, 0, 80, 17));
		
		int var3;

		for (var3 = 0; var3 < 3; ++var3)
		{
			for (int var4 = 0; var4 < 9; ++var4)
			{
				this.addSlotToContainer(new Slot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 155 + var3 * 18));
			}
		}

		for (var3 = 0; var3 < 9; ++var3)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, var3, 8 + var3 * 18, 213));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return encoder.isUseableByPlayer(player);
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

			if (slot >= 1)
			{
				if (this.getSlot(0).isItemValid(slotStack))
				{
					if (!this.mergeItemStack(slotStack, 0, 1, false)) { return null; }
				}
			}
			else if (!this.mergeItemStack(slotStack, this.containingItems.length, 37, false)) { return null; }

			if (slotStack.stackSize == 0)
			{
				slotObj.putStack((ItemStack) null);
			}
			else
			{
				slotObj.onSlotChanged();
			}

			if (slotStack.stackSize == copyStack.stackSize) { return null; }

			slotObj.onPickupFromSlot(player, slotStack);
		}

		return copyStack;
	}
}
