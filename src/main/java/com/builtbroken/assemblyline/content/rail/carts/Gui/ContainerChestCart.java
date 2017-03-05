package com.builtbroken.assemblyline.content.rail.carts.Gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/22/2016.
 */
public class ContainerChestCart extends Container
{
    private IInventory inventory;

    public ContainerChestCart(EntityPlayer player, IInventory inventory)
    {
        this.inventory = inventory;
        int numRows = inventory.getSizeInventory() / 9;
        inventory.openInventory();
        int playerInventoryStartY = (numRows - 4) * 18;
        int row;
        int col;

        //Chest inventory
        for (row = 0; row < numRows; ++row)
        {
            for (col = 0; col < 9; ++col)
            {
                this.addSlotToContainer(new Slot(inventory, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        //Player inventory
        for (row = 0; row < 3; ++row)
        {
            for (col = 0; col < 9; ++col)
            {
                this.addSlotToContainer(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + playerInventoryStartY));
            }
        }

        //Player quick bar
        for (row = 0; row < 9; ++row)
        {
            this.addSlotToContainer(new Slot(player.inventory, row, 8 + row * 18, 161 + playerInventoryStartY));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.inventory.isUseableByPlayer(p_75145_1_);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int moveSlot)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(moveSlot);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (moveSlot < inventory.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, inventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, inventory.getSizeInventory(), false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        this.inventory.closeInventory();
    }
}
