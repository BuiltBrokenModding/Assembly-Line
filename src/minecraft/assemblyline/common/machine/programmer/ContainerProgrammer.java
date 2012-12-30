package assemblyline.common.machine.programmer;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ContainerProgrammer extends Container implements IInventory
{
	private ItemStack[] containingItems = new ItemStack[3];
	private World worldObj;
	private Vector3 position;
	private InventoryPlayer inventoryPlayer;

	public ContainerProgrammer(InventoryPlayer inventoryPlayer, World worldObj, Vector3 position)
	{
		this.worldObj = worldObj;
		this.position = position;
		this.inventoryPlayer = inventoryPlayer;

		// Paper Input
		this.addSlotToContainer(new SlotDisk(this, 0, 42, 24));
		// Item Stamp
		this.addSlotToContainer(new Slot(this, 1, 78, 24));
		// Output Filter
		this.addSlotToContainer(new SlotDiskResult(this, 2, 136, 24));

		int var3;

		for (var3 = 0; var3 < 3; ++var3)
		{
			for (int var4 = 0; var4 < 9; ++var4)
			{
				this.addSlotToContainer(new Slot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
			}
		}

		for (var3 = 0; var3 < 9; ++var3)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, var3, 8 + var3 * 18, 142));
		}
	}

	@Override
	public void updateCraftingResults()
	{
		super.updateCraftingResults();
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
				setInventorySlotContents(0, null); // Prevents disk from being duplicated
			}

			if (slot > 2)
			{
				if (this.getSlot(0).isItemValid(slotStack))
				{
					if (!this.mergeItemStack(slotStack, 0, 1, false)) { return null; }
				}
				else if (!this.mergeItemStack(slotStack, 1, 2, false)) { return null; }
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

		onInventoryChanged();

		return copyStack;
	}

	@Override
	public int getSizeInventory()
	{
		return this.containingItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return this.containingItems[slot];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (this.containingItems[slot] != null)
		{
			ItemStack var3 = this.containingItems[slot];
			this.containingItems[slot] = null;
			return var3;
		}
		else
		{
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (this.containingItems[slot] != null && slot != 2)
		{
			ItemStack var2 = this.containingItems[slot];
			this.containingItems[slot] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		if (par1 < this.containingItems.length)
		{
			this.containingItems[par1] = par2ItemStack;
		}
	}

	@Override
	public String getInvName()
	{
		return "Programmer";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public void onInventoryChanged()
	{
		/**
		 * Makes the stamping recipe for disks
		 */
		boolean didStamp = false;

		if (this.getStackInSlot(0) != null && this.getStackInSlot(1) != null)
		{
			if (this.getStackInSlot(0).getItem() instanceof ItemDisk)
			{
				ItemStack outputStack = this.getStackInSlot(0).copy();
				outputStack.stackSize = 1;
				ArrayList<String> commands = ItemDisk.getCommands(outputStack);
				boolean filteringItemExists = false;

				for (String command : commands)
				{
					// remove commands
				}

				if (!filteringItemExists)
				{
					// add commands
				}

				ItemDisk.setCommands(outputStack, commands);
				this.setInventorySlotContents(2, outputStack);
				didStamp = true;
			}
		}

		if (!didStamp)
		{
			this.setInventorySlotContents(2, null);
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
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
	public void onCraftGuiClosed(EntityPlayer player)
	{
		super.onCraftGuiClosed(player);

		if (!this.worldObj.isRemote)
		{
			for (int slot = 0; slot < this.getSizeInventory(); ++slot)
			{
				ItemStack itemStack = this.getStackInSlotOnClosing(slot);

				if (itemStack != null && slot != 4)
				{
					player.dropPlayerItem(itemStack);
				}
			}
		}
	}
}
