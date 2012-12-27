package assemblyline.common.machine.filter;

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
import universalelectricity.core.vector.Vector3;

public class ContainerStamper extends Container implements IInventory
{
	private ItemStack[] containingItems = new ItemStack[5];
	private World worldObj;
	private Vector3 position;
	private InventoryPlayer inventoryPlayer;

	public ContainerStamper(InventoryPlayer inventoryPlayer, World worldObj, Vector3 position)
	{
		this.worldObj = worldObj;
		this.position = position;
		this.inventoryPlayer = inventoryPlayer;

		// Paper Input
		this.addSlotToContainer(new SlotFilter(this, 0, 42, 24));
		// Item Stamp
		this.addSlotToContainer(new Slot(this, 1, 78, 24));
		// Output Filter
		this.addSlotToContainer(new SlotFilterResult(this, 2, 136, 24));
		// Crafting Slot
		this.addSlotToContainer(new SlotFilter(this, 3, 78, 53));
		this.addSlotToContainer(new SlotCraftingResult(this, this, 4, 136, 53));

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
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1)
	{
		ItemStack itemStack3 = null;
		Slot itemStack = (Slot) this.inventorySlots.get(par1);

		if (itemStack != null && itemStack.getHasStack())
		{
			ItemStack itemStack2 = itemStack.getStack();
			itemStack3 = itemStack2.copy();

			if (par1 > 4)
			{
				if (this.getSlot(0).isItemValid(itemStack2))
				{
					if (!this.mergeItemStack(itemStack2, 0, 1, false)) { return null; }
				}
				else if (!this.mergeItemStack(itemStack2, 1, 2, false)) { return null; }
			}
			else if (!this.mergeItemStack(itemStack2, this.containingItems.length, 37, false)) { return null; }

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
		/**
		 * Makes the stamping recipe for filters
		 */
		boolean didStamp = false;

		if (this.getStackInSlot(0) != null && this.getStackInSlot(1) != null)
		{
			if (this.getStackInSlot(0).getItem() instanceof ItemFilter)
			{
				ItemStack outputStack = this.getStackInSlot(0).copy();
				outputStack.stackSize = 1;
				ArrayList<ItemStack> filters = ItemFilter.getFilters(outputStack);
				boolean failedCheck = false;

				for (ItemStack filteredStack : filters)
				{
					if (filteredStack.isItemEqual(this.getStackInSlot(1)))
					{
						failedCheck = true;
					}
				}

				if (!failedCheck)
				{
					filters.add(this.getStackInSlot(1));
					ItemFilter.setFilters(outputStack, filters);
					this.setInventorySlotContents(2, outputStack);
					didStamp = true;
				}
			}
		}

		if (!didStamp)
		{
			this.setInventorySlotContents(2, null);
		}

		/**
		 * TODO WORK IN PROGRESS. Make filters able to autocraft into its item based on what is in the player's inventory

		boolean didCraft = false;

		if (this.getStackInSlot(3) != null)
		{
			if (this.getStackInSlot(3).getItem() instanceof ItemFilter)
			{
				ArrayList<ItemStack> filters = ItemFilter.getFilters(this.getStackInSlot(3));

				if (filters.size() > 0)
				{
					ItemStack outputStack = filters.get(0);

					if (outputStack != null)
					{
						if (this.getIdealRecipe(outputStack) != null)
						{
							this.setInventorySlotContents(4, outputStack);
							didCraft = true;
						}
					}
				}
			}
		}

		if (!didCraft)
		{
			this.setInventorySlotContents(4, null);
		} */
	}

	/**
	 * Does this player's inventory contain the required resources to craft this item?
	 * 
	 * @return Required Items
	 */
	public ItemStack[] getIdealRecipe(ItemStack outputItem)
	{
		for (Object object : CraftingManager.getInstance().getRecipeList())
		{
			if (object instanceof IRecipe)
			{
				if (((IRecipe) object).getRecipeOutput() != null)
				{
					if (outputItem.isItemEqual(((IRecipe) object).getRecipeOutput()))
					{
						if (object instanceof ShapedRecipes)
						{
							if (this.doesMatch(((ShapedRecipes) object).recipeItems, outputItem)) { return ((ShapedRecipes) object).recipeItems; }
						}
						else if (object instanceof ShapelessRecipes)
						{
							if (this.doesMatch(((ShapelessRecipes) object).recipeItems.toArray(), outputItem)) { return (ItemStack[]) ((ShapelessRecipes) object).recipeItems.toArray(); }
						}
					}
				}
			}
		}

		return null;
	}

	private boolean doesMatch(Object[] recipeItems, ItemStack outputItem)
	{
		int itemMatch = 0;

		for (Object obj : recipeItems)
		{
			if (obj instanceof ItemStack)
			{
				ItemStack recipeItem = (ItemStack) obj;

				if (recipeItem != null)
				{
					for (int i = 0; i < this.inventoryPlayer.getSizeInventory(); i++)
					{
						ItemStack checkStack = this.inventoryPlayer.getStackInSlot(i);

						if (checkStack != null)
						{
							if (recipeItem.isItemEqual(checkStack))
							{
								// TODO Do NBT CHecking
								itemMatch++;
							}
						}
					}
				}
			}
		}

		return itemMatch >= recipeItems.length;
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
