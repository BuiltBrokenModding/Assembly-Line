package assemblyline.common.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.relauncher.ReflectionHelper;
import dark.library.helpers.Pair;

/**
 * Rewrite of the imprinter crafting system into its own manageable class
 * 
 * @author DarkGuardsman
 * 
 */
public class AutoCraftingManager
{
	boolean doDebug = false;
	TileEntity craftingEntity;
	IInventory craftingInv;

	/**
	 * The entity must be an instance of IInventory to pass only the tileEntity
	 */
	public AutoCraftingManager(final IAutoCrafter entity)
	{
		this.craftingEntity = (TileEntity) entity;
		if (entity instanceof IInventory)
		{
			this.craftingInv = (IInventory) entity;
		}
	}

	/**
	 * Use only the entity if it is also an instance of IInventory
	 */
	public AutoCraftingManager(final IAutoCrafter entity, final IInventory inv)
	{
		this(entity);
		if (inv != null)
		{
			this.craftingInv = inv;
		}
	}

	public void printDebug(String pre, String msg)
	{
		if (doDebug)
		{
			System.out.println("[AutoCrafter]: " + pre + " > " + msg);
		}
	}

	/**
	 * Does this player's inventory contain the required resources to craft this item?
	 * 
	 * @return Required items to make the desired item.
	 */
	public Pair<ItemStack, ItemStack[]> getIdealRecipe(ItemStack outputItem)
	{
		this.printDebug("IdealRecipe", outputItem.toString());

		for (Object object : CraftingManager.getInstance().getRecipeList())
		{
			if (object instanceof IRecipe)
			{
				if (((IRecipe) object).getRecipeOutput() != null)
				{
					if (this.areStacksEqual(outputItem, ((IRecipe) object).getRecipeOutput()))
					{
						this.printDebug("IdealRecipe", "Output Match Found");
						if (object instanceof ShapedRecipes)
						{
							if (this.hasResource(((ShapedRecipes) object).recipeItems) != null)
							{
								this.printDebug("IdealRecipe", "Shaped Recipe Found");
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), ((ShapedRecipes) object).recipeItems);
							}
						}
						else if (object instanceof ShapelessRecipes)
						{
							if (this.hasResource(((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1])) != null)
							{
								this.printDebug("IdealRecipe", "Shapeless Recipe Found");
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
								this.printDebug("IdealRecipe", "ShapedOre Recipe Found");
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
								this.printDebug("IdealRecipe", "ShapelessOre Recipe Found");
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
	 * Gets the itemStacks in the inv based on slots
	 * 
	 * @param inv - @IInventory instance
	 * @param slots - slot # to be used
	 * @return array of itemStack the same size as the slots input array
	 */
	public ItemStack[] getInvItems(IInventory inv, int... slots)
	{
		ItemStack[] containingItems = new ItemStack[slots.length];

		for (int slot = 0; slot < slots.length; slot++)
		{
			containingItems[slot] = inv.getStackInSlot(slots[slot]);
		}

		return containingItems;
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
			ItemStack[] containingItems = this.getInvItems(this.craftingInv, ((IAutoCrafter) this.craftingEntity).getCraftingInv());

			this.printDebug("ResourceChecker", "Looking for items");
			for (int i = 0; i < recipeItems.length && this.doDebug; i++)
			{
				this.printDebug("ResourceChecker", "ResourceChecker: Looking for " + recipeItems.toString());
			}
			/**
			 * The actual amount of resource required. Each ItemStack will only have stacksize of 1.
			 */
			ArrayList<ItemStack> actualResources = new ArrayList<ItemStack>();
			int itemMatch = 0;
			int itemInList = 0;
			for (Object obj : recipeItems)
			{
				itemInList++;
				if (obj instanceof ItemStack)
				{
					ItemStack recipeItem = (ItemStack) obj;
					actualResources.add(recipeItem.copy());
					if (recipeItem != null)
					{
						this.printDebug("ResourceChecker", "Item0" + itemInList + " = " + recipeItem.toString());
						int match = this.doesItemExist(recipeItem, containingItems);
						if (match != -2)
						{
							containingItems[match] = this.decrStackSize(containingItems[match], recipeItem.stackSize);
							this.printDebug("ResourceChecker", "Match found");
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

					this.printDebug("ResourceChecker", "Obj0" + itemInList + " = " + obj.toString());

					for (int x = 0; x < ingredientsArray.length; x++)
					{
						if (ingredientsArray[x] != null && ingredientsArray[x] instanceof ItemStack)
						{
							ItemStack recipeItem = (ItemStack) ingredientsArray[x];
							actualResources.add(recipeItem.copy());

							if (recipeItem != null)
							{
								int match = this.doesItemExist(recipeItem, containingItems);
								if (match != -2)
								{
									containingItems[match] = this.decrStackSize(containingItems[match], recipeItem.stackSize);
									itemMatch++;
									break;
								}
							}
						}
					}
				}
				else
				{
					this.printDebug("ResourceChecker", "Item0" + itemInList + " = null");
					itemMatch++;
				}
			}
			boolean resourcesFound = itemMatch >= actualResources.size();
			this.printDebug("ResourceChecker", "Found " + actualResources.size() + " Items and " + itemMatch + " slot matches");
			this.printDebug("ResourceChecker", "has all resources been found? /n A: " + resourcesFound);
			return resourcesFound ? actualResources : null;
		}
		catch (Exception e)
		{
			System.out.println("Failed to find recipes in the imprinter.");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Decreases the stack by a set amount
	 * 
	 * @param stack - starting stack
	 * @param amount - amount of items
	 * @return the edited stack
	 */
	public ItemStack decrStackSize(ItemStack stack, int amount)
	{
		if (stack != null)
		{
			if (stack.stackSize <= amount)
			{
				return null;
			}
			else
			{
				stack = stack.splitStack(amount);

				if (stack.stackSize == 0)
				{
					return null;
				}
				return stack;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * Checks if an item exist within the inv array
	 * 
	 * @param recipeItem - itemstack being searched for
	 * @param containingItems - inv array containing the search bounds
	 * @return the point in the array the item was found -1 = the item was null or not valid -2 =
	 * the item was not found
	 */
	private int doesItemExist(ItemStack recipeItem, ItemStack[] containingItems)
	{
		if (recipeItem == null || recipeItem.itemID == 0 || recipeItem.stackSize <= 0)
		{
			return -1;
		}
		this.printDebug("ResourceChecker", "Checking inv for item " + recipeItem.toString());
		for (int i = 0; i < containingItems.length; i++)
		{
			ItemStack checkStack = containingItems[i];

			if (checkStack != null)
			{
				this.printDebug("ResourceChecker", " -----Item in slot0" + i + " = " + checkStack.toString());
				if (areStacksEqual(recipeItem, checkStack))
				{
					this.printDebug("ResourceChecker", "Found matching item " + checkStack.toString());
					return i;
				}
			}
		}

		return -2;
	}

	/**
	 * Checks if itemstack are equal based on crafting result rather than normal itemstack this is
	 * done so that if the itemstack returns with
	 * 
	 * @param recipeItem - itemstack being compared
	 * @param checkStack - itemstack being comparted
	 * @return true if the items are a match for each other
	 * 
	 * If the item can't be stack and is able to take damage the item will be check on damaged
	 * status
	 * 
	 * If the item's meta data is not normal or in other words equals 32767 the meta data will be
	 * ignored
	 */
	public boolean areStacksEqual(ItemStack recipeItem, ItemStack checkStack)
	{
		if (recipeItem.itemID < Block.blocksList.length && recipeItem.getItemDamage() == 32767)
		{
			return recipeItem.itemID == checkStack.itemID;
		}
		return recipeItem.isItemEqual(checkStack) || (recipeItem.itemID == checkStack.itemID && recipeItem.isItemStackDamageable() && !recipeItem.isItemDamaged());
	}
}
