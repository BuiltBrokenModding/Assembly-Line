package assemblyline.common.machine.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import assemblyline.api.IArmbot;
import assemblyline.api.IArmbotUseable;
import assemblyline.common.AssemblyLine;
import assemblyline.common.Pair;
import assemblyline.common.machine.armbot.TileEntityArmbot;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class TileEntityImprinter extends TileEntityAdvanced implements ISidedInventory, IArmbotUseable
{
	public static final int IMPRINTER_MATRIX_START = 9;
	public static final int INVENTORY_START = IMPRINTER_MATRIX_START + 3;

	/**
	 * 9 slots for crafting, 1 slot for an imprint, 1 slot for an item
	 */
	public ItemStack[] craftingMatrix = new ItemStack[9];

	public ItemStack[] imprinterMatrix = new ItemStack[3];

	/**
	 * The Imprinter inventory containing slots.
	 */
	public ItemStack[] containingItems = new ItemStack[18];

	public ContainerImprinter container;

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		if (side == ForgeDirection.UP || side == ForgeDirection.DOWN)
			return 3;
		return imprinterMatrix.length;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		if (side == ForgeDirection.UP || side == ForgeDirection.DOWN) { return 1; }
		return containingItems.length;
	}

	@Override
	public int getSizeInventory()
	{
		return this.craftingMatrix.length + this.imprinterMatrix.length + this.containingItems.length;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack)
	{
		if (slot < this.getSizeInventory())
		{
			if (slot < IMPRINTER_MATRIX_START)
			{
				this.craftingMatrix[slot] = itemStack;
			}
			else if (slot < INVENTORY_START)
			{
				this.imprinterMatrix[slot - IMPRINTER_MATRIX_START] = itemStack;
			}
			else
			{
				this.containingItems[slot - INVENTORY_START] = itemStack;
			}
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot < IMPRINTER_MATRIX_START)
		{
			return this.craftingMatrix[slot];
		}
		else if (slot < INVENTORY_START)
		{
			return this.imprinterMatrix[slot - IMPRINTER_MATRIX_START];
		}
		else
		{
			return this.containingItems[slot - INVENTORY_START];
		}
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int i, int amount)
	{
		if (this.getStackInSlot(i) != null)
		{
			ItemStack var3;

			if (this.getStackInSlot(i).stackSize <= amount)
			{
				var3 = this.getStackInSlot(i);
				this.setInventorySlotContents(i, null);
				return var3;
			}
			else
			{
				var3 = this.getStackInSlot(i).splitStack(amount);

				if (this.getStackInSlot(i).stackSize == 0)
				{
					this.setInventorySlotContents(i, null);
				}

				return var3;
			}
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
		if (this.getStackInSlot(slot) != null)
		{
			ItemStack var2 = this.getStackInSlot(slot);
			this.setInventorySlotContents(slot, null);
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public String getInvName()
	{
		return TranslationHelper.getLocal("tile.imprinter.name");
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
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

	/**
	 * Updates all the output slots.
	 */
	@Override
	public void onInventoryChanged()
	{
		/**
		 * Makes the stamping recipe for filters
		 */
		boolean didStamp = false;

		if (this.imprinterMatrix[0] != null && this.imprinterMatrix[1] != null)
		{
			if (this.imprinterMatrix[0].getItem() instanceof ItemImprinter)
			{
				ItemStack outputStack = this.imprinterMatrix[0].copy();
				outputStack.stackSize = 1;
				ArrayList<ItemStack> filters = ItemImprinter.getFilters(outputStack);
				boolean filteringItemExists = false;

				for (ItemStack filteredStack : filters)
				{
					if (filteredStack.isItemEqual(this.imprinterMatrix[1]))
					{
						filters.remove(filteredStack);
						filteringItemExists = true;
						break;
					}
				}

				if (!filteringItemExists)
				{
					filters.add(this.imprinterMatrix[1]);
				}

				ItemImprinter.setFilters(outputStack, filters);
				this.imprinterMatrix[2] = outputStack;
				didStamp = true;
			}
		}

		if (!didStamp)
		{
			this.imprinterMatrix[2] = null;

			/**
			 * Try to craft from crafting grid. If not possible, then craft from imprint.
			 */
			boolean didCraft = false;

			/**
			 * Simulate an Inventory Crafting Instance
			 */

			if (this.container != null)
			{
				InventoryCrafting inventoryCrafting = new InventoryCrafting(this.container, 3, 3);

				for (int i = 0; i < this.craftingMatrix.length; i++)
				{
					inventoryCrafting.setInventorySlotContents(i, this.craftingMatrix[i]);
				}
				
				ItemStack matrixOutput = CraftingManager.getInstance().findMatchingRecipe(inventoryCrafting, this.worldObj);

				if (matrixOutput != null)
				{
					this.imprinterMatrix[2] = matrixOutput;
					didCraft = true;
				}
			}

			if (this.imprinterMatrix[0] != null && !didCraft)
			{
				if (this.imprinterMatrix[0].getItem() instanceof ItemImprinter)
				{
					ArrayList<ItemStack> filters = ItemImprinter.getFilters(this.imprinterMatrix[0]);

					for (ItemStack outputStack : filters)
					{
						if (outputStack != null)
						{
							Pair<ItemStack, ItemStack[]> idealRecipe = this.getIdealRecipe(outputStack);

							if (idealRecipe != null)
							{
								ItemStack recipeOutput = idealRecipe.getKey();

								if (recipeOutput != null & recipeOutput.stackSize > 0)
								{
									this.imprinterMatrix[2] = null;
									didCraft = true;
									break;
								}
							}
						}
					}
				}
			}

			if (!didCraft)
			{
				this.imprinterMatrix[2] = null;
			}
		}
	}
	
	public void onPickUpFromResult(EntityPlayer entityPlayer, ItemStack itemStack)
	{
		if (itemStack != null)
		{
			ItemStack[] requiredItems = this.getIdealRecipe(itemStack).getValue().clone();

			if (requiredItems != null)
			{
				for (ItemStack searchStack : requiredItems)
				{
					for (int i = 0; i < this.getSizeInventory(); i++)
					{
						ItemStack checkStack = this.getStackInSlot(i);

						if (checkStack != null)
						{
							if (searchStack.isItemEqual(checkStack) || (searchStack.itemID == checkStack.itemID && searchStack.getItemDamage() < 0))
							{
								this.decrStackSize(i, 1);
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Does this player's inventory contain the required resources to craft this item?
	 * 
	 * @return Required Items
	 */
	public Pair<ItemStack, ItemStack[]> getIdealRecipe(ItemStack outputItem)
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
							if (this.hasResource(((ShapedRecipes) object).recipeItems) != null) { return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), ((ShapedRecipes) object).recipeItems); }
						}
						else if (object instanceof ShapelessRecipes)
						{
							if (this.hasResource(((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1])) != null) { return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), (ItemStack[]) ((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1])); }
						}
						else if (object instanceof ShapedOreRecipe)
						{
							ShapedOreRecipe oreRecipe = (ShapedOreRecipe) object;
							Object[] oreRecipeInput = (Object[]) ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, oreRecipe, "input");

							ArrayList<ItemStack> hasResources = this.hasResource(oreRecipeInput);

							if (hasResources != null) {

							return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1])); }
						}
						else if (object instanceof ShapelessOreRecipe)
						{
							ShapelessOreRecipe oreRecipe = (ShapelessOreRecipe) object;
							ArrayList oreRecipeInput = (ArrayList) ReflectionHelper.getPrivateValue(ShapelessOreRecipe.class, oreRecipe, "input");

							List<ItemStack> hasResources = this.hasResource(oreRecipeInput.toArray());

							if (hasResources != null) { return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1])); }
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns if the following inventory has the following resource required.
	 * 
	 * @param recipeItems - The items to be checked for the recipes.
	 */
	public ArrayList<ItemStack> hasResource(Object[] recipeItems)
	{
		/**
		 * Simulate an imprinter.
		 */
		TileEntityImprinter dummyImprinter = new TileEntityImprinter();
		NBTTagCompound cloneData = new NBTTagCompound();
		this.writeToNBT(cloneData);
		dummyImprinter.readFromNBT(cloneData);

		/**
		 * The actual amount of resource required. Each ItemStack will only have stacksize of 1.
		 */
		ArrayList<ItemStack> actualResources = new ArrayList<ItemStack>();
		int itemMatch = 0;

		for (Object obj : recipeItems)
		{
			if (obj instanceof ItemStack)
			{
				ItemStack recipeItem = (ItemStack) obj;
				actualResources.add(recipeItem.copy());

				if (recipeItem != null)
				{
					for (int i = this.containingItems.length; i < this.containingItems.length; i++)
					{
						ItemStack checkStack = this.containingItems[i];

						if (checkStack != null)
						{
							if (recipeItem.isItemEqual(checkStack) || (recipeItem.itemID == checkStack.itemID && recipeItem.getItemDamage() < 0))
							{
								// TODO Do NBT Checking
								dummyImprinter.decrStackSize(i, 1);
								itemMatch++;
								break;
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
						actualResources.add(recipeItem.copy());

						if (recipeItem != null)
						{
							for (int i = this.containingItems.length; i < this.containingItems.length; i++)
							{
								ItemStack checkStack = this.containingItems[i];

								if (checkStack != null)
								{
									if (recipeItem.isItemEqual(checkStack) || (recipeItem.itemID == checkStack.itemID && recipeItem.getItemDamage() < 0))
									{
										// TODO Do NBT CHecking
										dummyImprinter.decrStackSize(i, 1);
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

		return itemMatch >= actualResources.size() ? actualResources : null;
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		NBTTagList var2 = nbt.getTagList("Items");

		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < var2.tagCount(); ++i)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(i);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.getSizeInventory())
			{
				this.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(var4));
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		NBTTagList var2 = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			if (this.getStackInSlot(i) != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) i);
				this.getStackInSlot(i).writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		nbt.setTag("Items", var2);
	}

	/**
	 * Armbot
	 * 
	 * @param tileEntity
	 * @param heldEntity
	 * @return
	 */
	@Override
	public boolean onUse(IArmbot armbot)
	{
		TileEntityArmbot armbotTile = (TileEntityArmbot) armbot;

		if (armbotTile.getGrabbedEntities().size() > 0)
		{
			Entity heldEntity = armbot.getGrabbedEntities().get(0);

			if (heldEntity != null)
			{
				if (heldEntity instanceof EntityItem)
				{
					ItemStack stack = ((EntityItem) heldEntity).getEntityItem();
					if (this.getStackInSlot(3) == null && stack != null && stack.itemID == AssemblyLine.itemImprint.itemID)
					{
						this.setInventorySlotContents(3, stack);
						this.onInventoryChanged();
						armbotTile.grabbedEntities.remove(0);
						return true;
					}
					else if (this.getStackInSlot(3) != null && stack != null)
					{
						ItemStack result = this.getStackInSlot(4); // crafting result
						if (result != null)
						{
							result = this.getStackInSlot(4);
							if (stack.isItemEqual(result))
							{
								if (result != null)
								{
									ItemStack[] requiredItems = this.getIdealRecipe(result).getValue().clone();

									if (requiredItems != null)
									{
										for (ItemStack searchStack : requiredItems)
										{
											for (int i = 0; i < this.getSizeInventory(); i++)
											{
												ItemStack checkStack = this.getStackInSlot(i);

												if (checkStack != null)
												{
													if (searchStack.isItemEqual(checkStack))
													{
														this.decrStackSize(i + INVENTORY_START, 1);
														break;
													}
												}
											}
										}
									}
								}
								if (stack.isStackable())
								{
									stack.stackSize += result.stackSize;
									this.onInventoryChanged();
									armbotTile.grabbedEntities.remove(0);
									armbotTile.grabbedEntities.add(new EntityItem(this.worldObj, this.xCoord, this.yCoord, this.zCoord, stack));
									return true;
								}
							}
						}
					}
				}
			}
			else
			{
				ItemStack result = this.getStackInSlot(4); // crafting result
				if (result != null)
				{
					result = this.getStackInSlot(4);
					if (result != null)
					{
						ItemStack[] requiredItems = this.getIdealRecipe(result).getValue().clone();

						if (requiredItems != null)
						{
							for (ItemStack searchStack : requiredItems)
							{
								for (int i = 0; i < this.getSizeInventory(); i++)
								{
									ItemStack checkStack = this.getStackInSlot(i);

									if (checkStack != null)
									{
										if (searchStack.isItemEqual(checkStack) || (searchStack.itemID == checkStack.itemID && searchStack.getItemDamage() < 0))
										{
											this.decrStackSize(i + INVENTORY_START, 1);
											break;
										}
									}
								}
							}
						}
					}
					this.onInventoryChanged();
					armbotTile.grabbedEntities.add(new EntityItem(this.worldObj, this.xCoord, this.yCoord, this.zCoord, result));
					return true;
				}
			}
		}

		return false;
	}

}
