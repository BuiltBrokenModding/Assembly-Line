package assemblyline.common.machine.sensor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerItemSensor extends Container
{
	protected TileItemSensor tileEntity;

	public ContainerItemSensor(InventoryPlayer inventoryPlayer, TileItemSensor te)
	{
		tileEntity = te;
		
		int numRows = 3;
        int var3 = (numRows - 4) * 18;
        int x;
        int y;

        for (x = 0; x < numRows; ++x)
        {
            for (y = 0; y < 9; ++y)
            {
                this.addSlotToContainer(new Slot(te, y + x * 9, 8 + y * 18, 18 + x * 18));
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
	
	public ItemStack func_82846_b(EntityPlayer player, int slotNum)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotNum);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (slotNum < 3 * 9)
            {
                if (!this.mergeItemStack(slotStack, 3 * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotStack, 0, 3 * 9, false))
            {
                return null;
            }

            if (slotStack.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tileEntity.isUseableByPlayer(player);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}
}