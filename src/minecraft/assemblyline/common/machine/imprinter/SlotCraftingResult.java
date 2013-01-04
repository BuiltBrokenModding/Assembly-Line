package assemblyline.common.machine.imprinter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCraftingResult extends Slot
{
	private ContainerImprinter container;

	public SlotCraftingResult(ContainerImprinter container, IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack itemStack)
	{
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return playerHasRequiredIngredients(player, getStack());
		// return true;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack par2ItemStack)
	{
		super.onPickupFromSlot(entityPlayer, par2ItemStack);

		if (this.getStack() != null)
		{
			ItemStack[] requiredItems = this.container.getIdealRecipe(this.getStack()).clone();

			if (requiredItems != null)
			{
				for (ItemStack searchStack : requiredItems)
				{
					for (int i = 0; i < entityPlayer.inventory.getSizeInventory(); i++)
					{
						ItemStack checkStack = entityPlayer.inventory.getStackInSlot(i);

						if (checkStack != null)
						{
							if (isItemEqual(searchStack, checkStack))
							{
								entityPlayer.inventory.decrStackSize(i, 1);
								break;
							}
						}
					}
				}
			}
		}
	}

	private boolean playerHasRequiredIngredients(EntityPlayer player, ItemStack desiredItem)
	{
		if (this.getStack() != null)
		{
			ItemStack[] idealRecipe = this.container.getIdealRecipe(this.getStack());
			if (idealRecipe != null)
			{
				ItemStack[] requiredItems = idealRecipe.clone();
				int foundItems = 0;

				if (requiredItems != null)
				{
					for (ItemStack searchStack : requiredItems)
					{
						for (int i = 0; i < player.inventory.getSizeInventory(); i++)
						{
							ItemStack checkStack = player.inventory.getStackInSlot(i);

							if (checkStack != null)
							{
								if (isItemEqual(searchStack, checkStack))
								{
									foundItems++;
								}
							}
						}
					}
				}

				if (foundItems >= requiredItems.length)
					return true;
			}
		}
		return false;
	}

	/**
	 * Checks if items are equal for recipe comparison. If either ItemStack's metadata is less than
	 * zero, meta automatically does not matter. This is because the Ore Dictionary puts "-1" onto a
	 * recipe ingredient whose metadata doesn't matter.
	 * 
	 * @param original the original item stack to test against
	 * @param test the item stack to test with
	 * @return whether or not the two stacks are equal
	 */
	public static boolean isItemEqual(ItemStack original, ItemStack test)
	{
		if (original.getItemDamage() < 0 || test.getItemDamage() < 0)
		{
			return original.itemID == test.itemID;
		}
		else
		{
			return original.itemID == test.itemID && original.getItemDamage() == test.getItemDamage();
		}
	}
}
