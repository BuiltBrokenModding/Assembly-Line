package assemblyline.common.machine.detector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDetector extends Container
{
	protected TileEntityDetector tileEntity;
	private int numRows = 3;

	public ContainerDetector(InventoryPlayer inventoryPlayer, TileEntityDetector tileEntity)
	{
		this.tileEntity = tileEntity;

		int var3 = (numRows - 4) * 18;
		int x;
		int y;

		for (x = 0; x < numRows; ++x)
		{
			for (y = 0; y < 9; ++y)
			{
				this.addSlotToContainer(new Slot(tileEntity, y + x * 9, 8 + y * 18, 18 + x * 18));
			}
		}

		for (x = 0; x < 3; ++x)
		{
			for (y = 0; y < 9; ++y)
			{
				this.addSlotToContainer(new Slot(inventoryPlayer, y + x * 9 + 9, 8 + y * 18, 103 + x * 18 + var3));
			}
		}

		for (x = 0; x < 9; ++x)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 161 + var3));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack var3 = null;
		Slot var4 = (Slot) this.inventorySlots.get(par2);

		if (var4 != null && var4.getHasStack())
		{
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();

			if (par2 < this.numRows * 9)
			{
				if (!this.mergeItemStack(var5, this.numRows * 9, this.inventorySlots.size(), true)) { return null; }
			}
			else if (!this.mergeItemStack(var5, 0, this.numRows * 9, false)) { return null; }

			if (var5.stackSize == 0)
			{
				var4.putStack((ItemStack) null);
			}
			else
			{
				var4.onSlotChanged();
			}
		}

		return var3;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tileEntity.isUseableByPlayer(player);
	}
}