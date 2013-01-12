package assemblyline.common.machine.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
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
}
