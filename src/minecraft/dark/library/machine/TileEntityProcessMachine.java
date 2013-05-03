package dark.library.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeDummyContainer;

public abstract class TileEntityProcessMachine extends TileEntityBasicMachine
{
	int[] TOP_SLOTS = { 0 };
	int[] WEST_SLOTS = { 1 };
	int[] EAST_SLOTS = { 1 };
	int[] NORTH_SLOTS = { 1 };
	int[] SOUTH_SLOTS = { 1 };
	int[] BOTTOM_SLOTS = { 2 };

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		switch (direction)
		{
			case UP:
				return TOP_SLOTS;
			case NORTH:
				return NORTH_SLOTS;
			case SOUTH:
				return SOUTH_SLOTS;
			case EAST:
				return EAST_SLOTS;
			case WEST:
				return WEST_SLOTS;
			case DOWN:
				return BOTTOM_SLOTS;
		}
		return null;
	}

	@Override
	public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3)
	{
		return this.isStackValidForSlot(par1, par2ItemStack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j)
	{
		return this.isStackValidForSlot(i, itemstack);
	}

	@Override
	public boolean isStackValidForSlot(int side, ItemStack itemstack)
	{
		return side == 2 ? false : (side == 1 ? isItemFuel(itemstack) : true);
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		if (ForgeDummyContainer.legacyFurnaceSides)
		{
			if (side == ForgeDirection.DOWN)
				return 1;
			if (side == ForgeDirection.UP)
				return 0;
			return 2;
		}
		else
		{
			if (side == ForgeDirection.DOWN)
				return 2;
			if (side == ForgeDirection.UP)
				return 0;
			return 1;
		}
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		switch (side)
		{
			case UP:
				return TOP_SLOTS != null ? TOP_SLOTS.length : 0;
			case NORTH:
				return NORTH_SLOTS != null ? NORTH_SLOTS.length : 0;
			case SOUTH:
				return SOUTH_SLOTS != null ? SOUTH_SLOTS.length : 0;
			case EAST:
				return EAST_SLOTS != null ? EAST_SLOTS.length : 0;
			case WEST:
				return WEST_SLOTS != null ? WEST_SLOTS.length : 0;
			case DOWN:
				return BOTTOM_SLOTS != null ? BOTTOM_SLOTS.length : 0;
		}
		return 0;
	}

	/**
	 * Return true if item is a fuel source (getItemBurnTime() > 0).
	 */
	public static boolean isItemFuel(ItemStack itemstack)
	{
		return TileEntityFurnace.getItemBurnTime(itemstack) > 0;
	}
}
