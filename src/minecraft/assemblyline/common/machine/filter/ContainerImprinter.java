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
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ContainerImprinter extends Container implements IInventory, ISlotWatcher
{
	private ItemStack[] containingItems = new ItemStack[5];
	private World worldObj;
	private Vector3 position;
	private InventoryPlayer inventoryPlayer;

	public ContainerImprinter(InventoryPlayer inventoryPlayer, World worldObj, Vector3 position)
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
				this.addSlotToContainer(new WatchedSlot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18, this));
			}
		}

		for (var3 = 0; var3 < 9; ++var3)
		{
			this.addSlotToContainer(new WatchedSlot(inventoryPlayer, var3, 8 + var3 * 18, 142, this));
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
				setInventorySlotContents(0, null); // Prevents filter from being duplicated
			}

			if (slot > 4)
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
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and
	 * returns them in a new stack.
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
	 * When some containers are closed they call this on each slot, then drop whatever it returns as
	 * an EntityItem - like when you close a workbench GUI.
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
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor
	 * sections).
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
				boolean filteringItemExists = false;

				for (ItemStack filteredStack : filters)
				{
					if (filteredStack.isItemEqual(this.getStackInSlot(1)))
					{
						filters.remove(filteredStack);
						filteringItemExists = true;
						break;
					}
				}

				if (!filteringItemExists)
				{
					filters.add(this.getStackInSlot(1));
				}

				ItemFilter.setFilters(outputStack, filters);
				this.setInventorySlotContents(2, outputStack);
				didStamp = true;
			}
		}

		if (!didStamp)
		{
			this.setInventorySlotContents(2, null);
		}

		// CRAFTING
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
		}

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
							if (this.doesMatch(((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1]), outputItem)) { return (ItemStack[]) ((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1]); }
						}
						else if (object instanceof ShapedOreRecipe)
						{
							ShapedOreRecipe oreRecipe = (ShapedOreRecipe) object;
							Object[] oreRecipeInput = (Object[]) ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, oreRecipe, "input");
							if (doesMatch(oreRecipeInput, outputItem))
							{
								ArrayList<ItemStack> finalRecipe = new ArrayList<ItemStack>();
								for (Object ingredientListObject : oreRecipeInput)
								{
									if (ingredientListObject != null)
									{
										if (ingredientListObject instanceof ArrayList)
										{
											ArrayList ingredientList = (ArrayList) ingredientListObject;
											for (Object ingredient : ingredientList)
											{
												if (ingredient != null)
												{
													if (ingredient instanceof ItemStack)
													{
														finalRecipe.add((ItemStack) ingredient);
													}
												}
											}
										}
									}
								}
								if (finalRecipe.size() == oreRecipeInput.length) { return finalRecipe.toArray(new ItemStack[1]); }
							}
						}
						else if (object instanceof ShapelessOreRecipe)
						{
							ShapelessOreRecipe oreRecipe = (ShapelessOreRecipe) object;
							ArrayList oreRecipeInput = (ArrayList) ReflectionHelper.getPrivateValue(ShapelessOreRecipe.class, oreRecipe, "input");
							if (doesMatch(oreRecipeInput.toArray(), outputItem))
							{
								ArrayList<ItemStack> finalRecipe = new ArrayList<ItemStack>();
								for (Object ingredientListObject : oreRecipeInput)
								{
									if (ingredientListObject != null)
									{
										if (ingredientListObject instanceof ArrayList)
										{
											ArrayList ingredientList = (ArrayList) ingredientListObject;
											for (Object ingredient : ingredientList)
											{
												if (ingredient != null)
												{
													if (ingredient instanceof ItemStack)
													{
														if (this.inventoryPlayer.hasItemStack((ItemStack) ingredient))
															finalRecipe.add((ItemStack) ingredient);
													}
												}
											}
										}
										else if (ingredientListObject instanceof ItemStack)
										{
											if (this.inventoryPlayer.hasItemStack((ItemStack) ingredientListObject))
												finalRecipe.add((ItemStack) ingredientListObject);
										}
									}
								}
								if (finalRecipe.size() == oreRecipeInput.size()) { return finalRecipe.toArray(new ItemStack[1]); }
							}
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
							if (SlotCraftingResult.isItemEqual(recipeItem, checkStack))
							{
								// TODO Do NBT CHecking
								itemMatch++;
							}
						}
					}
				}
			}
			else if (obj instanceof ArrayList)
			{
				ArrayList ingredientsList = (ArrayList) obj;
				Object[] ingredientsArray = ingredientsList.toArray();

				optionsLoop:
				for (int x = 0; x < ingredientsArray.length; x++)
				{
					if (ingredientsArray[x] != null && ingredientsArray[x] instanceof ItemStack)
					{
						ItemStack recipeItem = (ItemStack) ingredientsArray[x];

						if (recipeItem != null)
						{
							for (int i = 0; i < this.inventoryPlayer.getSizeInventory(); i++)
							{
								ItemStack checkStack = this.inventoryPlayer.getStackInSlot(i);

								if (checkStack != null)
								{
									if (SlotCraftingResult.isItemEqual(recipeItem, checkStack))
									{
										// TODO Do NBT CHecking
										itemMatch++;
										break optionsLoop;
									}
								}
							}
						}
					}
				}
			}
		}

		return itemMatch >= recipeItems.length;
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

	@Override
	public void slotContentsChanged()
	{
		onInventoryChanged();
	}
}
