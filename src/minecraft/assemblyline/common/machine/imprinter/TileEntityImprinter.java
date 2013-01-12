package assemblyline.common.machine.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
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
import assemblyline.common.Pair;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class TileEntityImprinter extends TileEntityAdvanced implements ISidedInventory
{
	/**
	 * Imprinter slots. 10 extra slots for storing imprints.
	 */
	private ItemStack[] containingItems = new ItemStack[5 + 10];

	public static final int START_INVENTORY = 5;

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		if (side == ForgeDirection.UP || side == ForgeDirection.DOWN) { return 3; }
		return 4;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 1;
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

	@Override
	public void onInventoryChanged()
	{
		/**
		 * Makes the stamping recipe for filters
		 */
		boolean didStamp = false;

		if (this.getStackInSlot(0) != null && this.getStackInSlot(1) != null)
		{
			if (this.getStackInSlot(0).getItem() instanceof ItemImprinter)
			{
				ItemStack outputStack = this.getStackInSlot(0).copy();
				outputStack.stackSize = 1;
				ArrayList<ItemStack> filters = ItemImprinter.getFilters(outputStack);
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

				ItemImprinter.setFilters(outputStack, filters);
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
			if (this.getStackInSlot(3).getItem() instanceof ItemImprinter)
			{
				ArrayList<ItemStack> filters = ItemImprinter.getFilters(this.getStackInSlot(3));

				if (filters.size() > 0)
				{
					ItemStack outputStack = filters.get(0);

					if (outputStack != null)
					{
						Pair<ItemStack, ItemStack[]> idealRecipe = this.getIdealRecipe(outputStack);

						if (idealRecipe != null)
						{
							this.setInventorySlotContents(4, idealRecipe.getKey());
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

							if (hasResources != null) { return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1])); }
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
	 * Returns if players has the following resource required.
	 * 
	 * @param recipeItems - The items to be checked for the recipes.
	 */
	private ArrayList<ItemStack> hasResource(Object[] recipeItems)
	{
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
					for (int i = START_INVENTORY; i < this.getSizeInventory(); i++)
					{
						ItemStack checkStack = this.getStackInSlot(i);

						if (checkStack != null)
						{
							if (SlotCraftingResult.isItemEqual(recipeItem, checkStack))
							{
								// TODO Do NBT CHecking
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
							for (int i = START_INVENTORY; i < this.getSizeInventory(); i++)
							{
								ItemStack checkStack = this.getStackInSlot(i);

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

		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.containingItems.length)
			{
				this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
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

		for (int var3 = 0; var3 < this.containingItems.length; ++var3)
		{
			if (this.containingItems[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.containingItems[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		nbt.setTag("Items", var2);
	}

}
