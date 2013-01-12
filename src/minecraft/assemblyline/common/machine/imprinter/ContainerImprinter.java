package assemblyline.common.machine.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import assemblyline.common.AssemblyLine;
import assemblyline.common.Pair;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ContainerImprinter extends Container implements ISlotWatcher
{
	private InventoryPlayer inventoryPlayer;
	public TileEntityImprinter tileEntity;

	public ContainerImprinter(InventoryPlayer inventoryPlayer, TileEntityImprinter tileEntity)
	{
		this.tileEntity = tileEntity;
		this.inventoryPlayer = inventoryPlayer;

		// Paper Input
		this.addSlotToContainer(new SlotCustom(this.tileEntity, 0, 33, 22, new ItemStack(AssemblyLine.itemImprint)));
		// Item Stamp
		this.addSlotToContainer(new Slot(this.tileEntity, 1, 69, 22));
		// Output Filter
		this.addSlotToContainer(new SlotImprintResult(this.tileEntity, 2, 127, 22));
		// Crafting Slot
		this.addSlotToContainer(new SlotCustom(this.tileEntity, 3, 69, 51, new ItemStack(AssemblyLine.itemImprint)));
		// Crafting Output
		this.addSlotToContainer(new SlotCraftingResult(this, this.tileEntity, 4, 127, 51));

		// Imprinter Inventory
		for (int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new WatchedSlot(this.tileEntity, i + TileEntityImprinter.START_INVENTORY, 8 + i * 18, 80, this));
		}

		// Player Inventory
		int var3;

		for (var3 = 0; var3 < 3; ++var3)
		{
			for (int var4 = 0; var4 < 9; ++var4)
			{
				this.addSlotToContainer(new WatchedSlot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 102 + var3 * 18, this));
			}
		}

		for (var3 = 0; var3 < 9; ++var3)
		{
			this.addSlotToContainer(new WatchedSlot(inventoryPlayer, var3, 8 + var3 * 18, 160, this));
		}
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

			if (slot == 2)
			{
				// Prevents filter from being duplicated
				this.tileEntity.setInventorySlotContents(0, null);
			}

			if (slot > this.tileEntity.getSizeInventory())
			{
				if (this.getSlot(0).isItemValid(slotStack))
				{
					if (!this.mergeItemStack(slotStack, 0, 1, false)) { return null; }
				}
				else if (!this.mergeItemStack(slotStack, this.tileEntity.START_INVENTORY, this.tileEntity.getSizeInventory(), false)) { return null; }
			}
			else if (!this.mergeItemStack(slotStack, this.tileEntity.getSizeInventory(), this.tileEntity.getSizeInventory() + 36, false)) { return null; }

			if (slotStack.stackSize == 0)
			{
				slotObj.putStack(null);
			}
			else
			{
				slotObj.onSlotChanged();
			}

			if (slotStack.stackSize == copyStack.stackSize) { return null; }

			slotObj.onPickupFromSlot(player, slotStack);
		}

		this.slotContentsChanged();

		return copyStack;
	}

	@Override
	public void slotContentsChanged()
	{
		this.tileEntity.onInventoryChanged();
	}
}
