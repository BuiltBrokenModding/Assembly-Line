package assemblyline.common.machine.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import assemblyline.api.IArmbot;
import assemblyline.api.IArmbotUseable;
import assemblyline.common.Pair;
import cpw.mods.fml.common.registry.GameRegistry;
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

	/**
	 * The containing currently used by the imprinter.
	 */
	public ContainerImprinter container;

	/**
	 * Is the current crafting result a result of an imprint?
	 */
	private boolean isImprinting = false;

	/**
	 * The ability for the imprinter to serach nearby inventories.
	 */
	public boolean searchInventories = true;

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return INVENTORY_START;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return containingItems.length;
	}

	@Override
	public int getSizeInventory()
	{
		return this.craftingMatrix.length + this.imprinterMatrix.length + this.containingItems.length;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor
	 * sections).
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
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and
	 * returns them in a new stack.
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
	 * When some containers are closed they call this on each slot, then drop whatever it returns as
	 * an EntityItem - like when you close a workbench GUI.
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
	 * Construct an InventoryCrafting Matrix on the fly.
	 * 
	 * @return
	 */
	public InventoryCrafting getCraftingMatrix()
	{
		if (this.container != null)
		{
			InventoryCrafting inventoryCrafting = new InventoryCrafting(this.container, 3, 3);

			for (int i = 0; i < this.craftingMatrix.length; i++)
			{
				inventoryCrafting.setInventorySlotContents(i, this.craftingMatrix[i]);
			}

			return inventoryCrafting;
		}

		return null;
	}

	public void replaceCraftingMatrix(InventoryCrafting inventoryCrafting)
	{
		for (int i = 0; i < this.craftingMatrix.length; i++)
		{
			this.craftingMatrix[i] = inventoryCrafting.getStackInSlot(i);
		}
	}

	public boolean isMatrixEmpty()
	{
		for (int i = 0; i < 9; i++)
		{
			if (this.craftingMatrix[i] != null)
				return false;
		}

		return true;
	}

	/**
	 * Updates all the output slots. Call this to update the Imprinter.
	 */
	@Override
	public void onInventoryChanged()
	{
		/**
		 * Makes the stamping recipe for filters
		 */
		this.isImprinting = false;

		if (this.isMatrixEmpty() && this.imprinterMatrix[0] != null && this.imprinterMatrix[1] != null)
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
				this.isImprinting = true;
			}
		}

		if (!this.isImprinting)
		{
			this.imprinterMatrix[2] = null;

			/**
			 * Try to craft from crafting grid. If not possible, then craft from imprint.
			 */
			boolean didCraft = false;

			/**
			 * Simulate an Inventory Crafting Instance
			 */
			InventoryCrafting inventoryCrafting = this.getCraftingMatrix();

			if (inventoryCrafting != null)
			{
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
									this.imprinterMatrix[2] = recipeOutput;
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
			if (this.isImprinting)
			{
				this.imprinterMatrix[0] = null;
			}
			else
			{
				/**
				 * Based on the ideal recipe, consume all the items required to make such recipe.
				 */
				if (this.getIdealRecipe(itemStack) != null)
				{
					ItemStack[] requiredItems = this.getIdealRecipe(itemStack).getValue().clone();

					if (requiredItems != null)
					{
						for (ItemStack searchStack : requiredItems)
						{
							if (searchStack != null)
							{
								inventories:
								for (IInventory inventory : getAvaliableInventories())
								{
									for (int i = 0; i < inventory.getSizeInventory(); i++)
									{
										ItemStack checkStack = inventory.getStackInSlot(i);

										if (checkStack != null)
										{
											if (searchStack.isItemEqual(checkStack) || (searchStack.itemID == checkStack.itemID && searchStack.getItemDamage() < 0))
											{
												inventory.decrStackSize(i, 1);
												break inventories;
											}
										}
									}
								}

								for (int i = 0; i < this.containingItems.length; i++)
								{
									ItemStack checkStack = this.containingItems[i];

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
				}
				else
				{
					try
					{
						/**
						 * Fail to, hence consume from crafting grid.
						 */
						InventoryCrafting inventoryCrafting = this.getCraftingMatrix();
						GameRegistry.onItemCrafted(entityPlayer, itemStack, inventoryCrafting);

						for (int var3 = 0; var3 < inventoryCrafting.getSizeInventory(); ++var3)
						{
							ItemStack var4 = inventoryCrafting.getStackInSlot(var3);

							if (var4 != null)
							{
								inventoryCrafting.decrStackSize(var3, 1);

								if (var4.getItem().hasContainerItem())
								{
									ItemStack var5 = var4.getItem().getContainerItemStack(var4);

									if (var5.isItemStackDamageable() && var5.getItemDamage() > var5.getMaxDamage())
									{
										MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(entityPlayer, var5));
										var5 = null;
									}

									if (var5 != null && (!var4.getItem().doesContainerItemLeaveCraftingGrid(var4) || !entityPlayer.inventory.addItemStackToInventory(var5)))
									{
										if (inventoryCrafting.getStackInSlot(var3) == null)
										{
											inventoryCrafting.setInventorySlotContents(var3, var5);
										}
										else
										{
											entityPlayer.dropPlayerItem(var5);
										}
									}
								}
							}
						}

						this.replaceCraftingMatrix(inventoryCrafting);
					}
					catch (Exception e)
					{
						System.out.println("Imprinter: Failed to craft");
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Does this player's inventory contain the required resources to craft this item?
	 * 
	 * @return Required items to make the desired item.
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
							if (this.hasResource(((ShapedRecipes) object).recipeItems) != null)
							{
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), ((ShapedRecipes) object).recipeItems);
							}
						}
						else if (object instanceof ShapelessRecipes)
						{
							if (this.hasResource(((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1])) != null)
							{
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), (ItemStack[]) ((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1]));
							}
						}
						else if (object instanceof ShapedOreRecipe)
						{
							ShapedOreRecipe oreRecipe = (ShapedOreRecipe) object;
							Object[] oreRecipeInput = (Object[]) ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, oreRecipe, "input");

							ArrayList<ItemStack> hasResources = this.hasResource(oreRecipeInput);

							if (hasResources != null)
							{
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1]));
							}
						}
						else if (object instanceof ShapelessOreRecipe)
						{
							ShapelessOreRecipe oreRecipe = (ShapelessOreRecipe) object;
							ArrayList oreRecipeInput = (ArrayList) ReflectionHelper.getPrivateValue(ShapelessOreRecipe.class, oreRecipe, "input");

							List<ItemStack> hasResources = this.hasResource(oreRecipeInput.toArray());

							if (hasResources != null)
							{
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1]));
							}
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
		try
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
						if (this.doesItemExist(recipeItem, dummyImprinter))
						{
							itemMatch++;
						}
					}
				}
				else if (obj instanceof ArrayList)
				{
					/**
					 * Look for various possible ingredients of the same item and try to match it.
					 */
					ArrayList ingredientsList = (ArrayList) obj;
					Object[] ingredientsArray = ingredientsList.toArray();

					for (int x = 0; x < ingredientsArray.length; x++)
					{
						if (ingredientsArray[x] != null && ingredientsArray[x] instanceof ItemStack)
						{
							ItemStack recipeItem = (ItemStack) ingredientsArray[x];
							actualResources.add(recipeItem.copy());

							if (recipeItem != null)
							{
								if (this.doesItemExist(recipeItem, dummyImprinter))
								{
									itemMatch++;
									break;
								}
							}
						}
					}
				}
			}

			return itemMatch >= actualResources.size() ? actualResources : null;
		}
		catch (Exception e)
		{
			System.out.println("Failed to find recipes in the imprinter.");
			e.printStackTrace();
		}

		return null;
	}

	private boolean doesItemExist(ItemStack recipeItem, TileEntityImprinter dummyImprinter)
	{
		for (int i = 0; i < dummyImprinter.containingItems.length; i++)
		{
			ItemStack checkStack = dummyImprinter.containingItems[i];

			if (checkStack != null)
			{
				if (recipeItem.isItemEqual(checkStack) || (recipeItem.itemID == checkStack.itemID && recipeItem.getItemDamage() < 0))
				{
					// TODO Do NBT Checking
					dummyImprinter.decrStackSize(i + INVENTORY_START, 1);
					return true;

				}
			}
		}

		for (IInventory inventory : getSimulatedAvaliableInventories())
		{
			for (int i = 0; i < inventory.getSizeInventory(); i++)
			{
				ItemStack checkStack = inventory.getStackInSlot(i);

				if (checkStack != null)
				{
					if (recipeItem.isItemEqual(checkStack) || (recipeItem.itemID == checkStack.itemID && recipeItem.getItemDamage() < 0))
					{
						// TODO Do NBT Checking
						inventory.decrStackSize(i, 1);
						return true;
					}
				}
			}
		}

		return false;
	}

	private List<IInventory> getAvaliableInventories()
	{
		List<IInventory> inventories = new ArrayList<IInventory>();

		if (this.searchInventories)
		{
			for (int side = 0; side < 6; side++)
			{
				Vector3 position = new Vector3(this);
				position.modifyPositionFromSide(ForgeDirection.getOrientation(side));
				TileEntity tileEntity = position.getTileEntity(this.worldObj);

				if (tileEntity != null)
				{
					/**
					 * Try to put items into a chest.
					 */
					if (tileEntity instanceof TileEntityMulti)
					{
						Vector3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;

						if (mainBlockPosition != null)
						{
							if (mainBlockPosition.getTileEntity(this.worldObj) instanceof IInventory)
							{
								inventories.add((IInventory) mainBlockPosition.getTileEntity(this.worldObj));
							}
						}
					}
					else if (tileEntity instanceof TileEntityChest)
					{
						inventories.add((TileEntityChest) tileEntity);

						/**
						 * Try to find a double chest.
						 */
						for (int i = 2; i < 6; i++)
						{
							ForgeDirection searchDirection = ForgeDirection.getOrientation(i);
							Vector3 searchPosition = position.clone();
							searchPosition.modifyPositionFromSide(searchDirection);

							if (searchPosition.getTileEntity(this.worldObj) != null)
							{
								if (searchPosition.getTileEntity(this.worldObj).getClass() == tileEntity.getClass())
								{
									inventories.add((TileEntityChest) searchPosition.getTileEntity(this.worldObj));
									break;
								}
							}
						}

					}
					else if (tileEntity instanceof IInventory && !(tileEntity instanceof TileEntityImprinter))
					{
						inventories.add((IInventory) tileEntity);
					}
				}
			}
		}

		return inventories;
	}

	private List<IInventory> getSimulatedAvaliableInventories()
	{
		List<IInventory> simulatedInventories = new ArrayList<IInventory>();

		/**
		 * Create a simulated version of all TileEntities.
		 */
		for (IInventory inventory : this.getAvaliableInventories())
		{
			if (inventory instanceof TileEntity)
			{
				TileEntity tileEntity = (TileEntity) inventory;

				try
				{
					// TODO: Get Client Side Working.
					TileEntity simulatedTileEntity = tileEntity.getClass().newInstance();
					simulatedTileEntity.worldObj = tileEntity.worldObj;

					NBTTagCompound cloneData = new NBTTagCompound();
					tileEntity.writeToNBT(cloneData);
					tileEntity.readFromNBT(cloneData);

					for (int i = 0; i < inventory.getSizeInventory(); i++)
					{
						ItemStack itemStack = inventory.getStackInSlot(i);

						if (itemStack != null)
						{
							itemStack = itemStack.copy();
						}

						((IInventory) simulatedTileEntity).setInventorySlotContents(i, itemStack);
					}

					simulatedInventories.add((IInventory) simulatedTileEntity);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return simulatedInventories;
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		NBTTagList var2 = nbt.getTagList("Items");
		this.craftingMatrix = new ItemStack[9];
		this.imprinterMatrix = new ItemStack[3];
		this.containingItems = new ItemStack[18];

		for (int i = 0; i < var2.tagCount(); ++i)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(i);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.getSizeInventory())
			{
				this.setInventorySlotContents(var5, ItemStack.loadItemStackFromNBT(var4));
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
	 * Tries to let the Armbot craft an item.
	 */
	@Override
	public boolean onUse(IArmbot armbot, String[] args)
	{
		this.onInventoryChanged();

		if (this.imprinterMatrix[2] != null)
		{
			armbot.grabItem(this.imprinterMatrix[2].copy());
			this.onPickUpFromResult(null, this.imprinterMatrix[2]);
			this.imprinterMatrix[2] = null;
		}

		return false;
	}

}
