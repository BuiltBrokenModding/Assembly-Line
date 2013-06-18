package assemblyline.common.machine;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import assemblyline.common.block.BlockCrate;
import assemblyline.common.block.TileEntityCrate;
import assemblyline.common.imprinter.ItemImprinter;

public class InvExtractionHelper
{
	World world;
	Vector3 location;
	List<ItemStack> filterItems;
	boolean inverted;

	public InvExtractionHelper(World world, Vector3 location, List<ItemStack> filters, boolean inverted)
	{
		this.world = world;
		this.location = location;
		this.filterItems = filters;
		if (filterItems == null)
		{
			filterItems = new ArrayList<ItemStack>();
		}
		this.inverted = inverted;
	}

	/**
	 * Throws the items from the manipulator into the world.
	 * 
	 * @param outputPosition
	 * @param items
	 */
	public void throwItem(Vector3 outputPosition, ItemStack items)
	{
		if (!world.isRemote)
		{
			EntityItem entityItem = new EntityItem(world, outputPosition.x + 0.5, outputPosition.y + 0.8, outputPosition.z + 0.5, items);
			entityItem.motionX = 0;
			entityItem.motionZ = 0;
			entityItem.motionY /= 5;
			entityItem.delayBeforeCanPickup = 20;
			world.spawnEntityInWorld(entityItem);
		}
	}

	/**
	 * Tries to place an itemStack in a specific position if it is an inventory.
	 * 
	 * @return The ItemStack remained after place attempt
	 */
	public ItemStack tryPlaceInPosition(ItemStack itemStack, Vector3 position, ForgeDirection direction)
	{
		TileEntity tileEntity = position.getTileEntity(world);

		if (tileEntity != null && itemStack != null)
		{
			/**
			 * Try to put items into a chest.
			 */
			if (tileEntity instanceof TileEntityMulti)
			{
				Vector3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;

				if (mainBlockPosition != null)
				{
					if (!(mainBlockPosition.getTileEntity(world) instanceof TileEntityMulti))
					{
						return tryPlaceInPosition(itemStack, mainBlockPosition, direction);
					}
				}
			}
			else if (tileEntity instanceof TileEntityChest)
			{
				TileEntityChest[] chests = { (TileEntityChest) tileEntity, null };

				/**
				 * Try to find a double chest.
				 */
				for (int i = 2; i < 6; i++)
				{
					ForgeDirection searchDirection = ForgeDirection.getOrientation(i);
					Vector3 searchPosition = position.clone();
					searchPosition.modifyPositionFromSide(searchDirection);

					if (searchPosition.getTileEntity(world) != null)
					{
						if (searchPosition.getTileEntity(world).getClass() == chests[0].getClass())
						{
							chests[1] = (TileEntityChest) searchPosition.getTileEntity(world);
							break;
						}
					}
				}

				for (TileEntityChest chest : chests)
				{
					if (chest != null)
					{
						for (int i = 0; i < chest.getSizeInventory(); i++)
						{
							itemStack = this.addStackToInventory(i, chest, itemStack);
							if (itemStack == null)
							{
								return null;
							}
						}
					}
				}
			}
			else if (tileEntity instanceof TileEntityCrate)
			{
				return BlockCrate.addStackToCrate((TileEntityCrate) tileEntity, itemStack);
			}
			else if (tileEntity instanceof ISidedInventory)
			{
				ISidedInventory inventory = (ISidedInventory) tileEntity;
				int[] slots = inventory.getAccessibleSlotsFromSide(direction.getOpposite().ordinal());
				for (int i = 0; i < slots.length; i++)
				{
					if (inventory.canInsertItem(slots[i], itemStack, direction.getOpposite().ordinal()))
					{
						itemStack = this.addStackToInventory(slots[i], inventory, itemStack);
					}
					if (itemStack == null)
					{
						return null;
					}
				}

			}
			else if (tileEntity instanceof net.minecraftforge.common.ISidedInventory)
			{
				net.minecraftforge.common.ISidedInventory inventory = (net.minecraftforge.common.ISidedInventory) tileEntity;

				int startIndex = inventory.getStartInventorySide(direction.getOpposite());

				for (int i = startIndex; i < startIndex + inventory.getSizeInventorySide(direction); i++)
				{
					itemStack = this.addStackToInventory(i, inventory, itemStack);
					if (itemStack == null)
					{
						return null;
					}
				}
			}
			else if (tileEntity instanceof IInventory)
			{
				IInventory inventory = (IInventory) tileEntity;

				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					itemStack = this.addStackToInventory(i, inventory, itemStack);
					if (itemStack == null)
					{
						return null;
					}
				}
			}
		}

		if (itemStack.stackSize <= 0)
		{
			return null;
		}

		return itemStack;
	}

	public ItemStack addStackToInventory(int slotIndex, IInventory inventory, ItemStack itemStack)
	{
		if (inventory.getSizeInventory() > slotIndex)
		{
			ItemStack stackInInventory = inventory.getStackInSlot(slotIndex);

			if (stackInInventory == null)
			{
				inventory.setInventorySlotContents(slotIndex, itemStack);
				if (inventory.getStackInSlot(slotIndex) == null)
				{
					return itemStack;
				}
				return null;
			}
			else if (stackInInventory.isItemEqual(itemStack) && stackInInventory.isStackable())
			{
				stackInInventory = stackInInventory.copy();
				int stackLim = Math.min(inventory.getInventoryStackLimit(), itemStack.getMaxStackSize());
				int rejectedAmount = Math.max((stackInInventory.stackSize + itemStack.stackSize) - stackLim, 0);
				stackInInventory.stackSize = Math.min(Math.max((stackInInventory.stackSize + itemStack.stackSize - rejectedAmount), 0), inventory.getInventoryStackLimit());
				itemStack.stackSize = rejectedAmount;
				inventory.setInventorySlotContents(slotIndex, stackInInventory);
			}
		}

		if (itemStack.stackSize <= 0)
		{
			return null;
		}

		return itemStack;
	}

	/**
	 * Tries to take a item from a inventory at a specific position.
	 * 
	 * @param position
	 * @return
	 */
	public ItemStack tryGrabFromPosition(Vector3 position, ForgeDirection direction, int ammount)
	{
		ItemStack returnStack = null;
		TileEntity tileEntity = position.getTileEntity(world);

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
					if (!(mainBlockPosition.getTileEntity(world) instanceof TileEntityMulti))
					{
						return tryGrabFromPosition(mainBlockPosition, direction, ammount);
					}
				}
			}
			else if (tileEntity instanceof TileEntityChest)
			{
				TileEntityChest[] chests = { (TileEntityChest) tileEntity, null };

				/**
				 * Try to find a double chest.
				 */
				for (int i = 2; i < 6; i++)
				{
					ForgeDirection searchDirection = ForgeDirection.getOrientation(i);
					Vector3 searchPosition = position.clone();
					searchPosition.modifyPositionFromSide(searchDirection);

					if (searchPosition.getTileEntity(world) != null)
					{
						if (searchPosition.getTileEntity(world).getClass() == chests[0].getClass())
						{
							chests[1] = (TileEntityChest) searchPosition.getTileEntity(world);
							break;
						}
					}
				}

				chestSearch:
				for (TileEntityChest chest : chests)
				{
					if (chest != null)
					{
						for (int i = 0; i < chest.getSizeInventory(); i++)
						{
							ItemStack itemStack = this.removeStackFromInventory(i, chest,ammount);

							if (itemStack != null)
							{
								returnStack = itemStack;
								break chestSearch;
							}
						}
					}
				}
			}
			else if (tileEntity instanceof ISidedInventory)
			{
				ISidedInventory inventory = (ISidedInventory) tileEntity;

				int[] slots = inventory.getAccessibleSlotsFromSide(direction.ordinal());

				for (int i = 0; i < slots.length; i++)
				{
					int slot = slots[i];
					ItemStack itemStack = this.removeStackFromInventory(i, inventory,ammount);
					if (itemStack != null && inventory.canExtractItem(slot, itemStack, direction.ordinal()))
					{
						returnStack = itemStack;
						break;
					}
				}
			}
			else if (tileEntity instanceof net.minecraftforge.common.ISidedInventory)
			{
				net.minecraftforge.common.ISidedInventory inventory = (net.minecraftforge.common.ISidedInventory) tileEntity;

				int startIndex = inventory.getStartInventorySide(direction);

				for (int i = startIndex; i < startIndex + inventory.getSizeInventorySide(direction); i++)
				{
					ItemStack itemStack = this.removeStackFromInventory(i, inventory,ammount);

					if (itemStack != null)
					{
						returnStack = itemStack;
						break;
					}
				}
			}
			else if (tileEntity instanceof IInventory)
			{
				IInventory inventory = (IInventory) tileEntity;

				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					ItemStack itemStack = this.removeStackFromInventory(i, inventory,ammount);
					if (itemStack != null)
					{
						returnStack = itemStack;
						break;
					}
				}
			}
		}

		return returnStack;
	}
	/**
	 * Takes an item from the given inventory
	 */
	public ItemStack removeStackFromInventory(int slotIndex, IInventory inventory, int ammount)
	{
		if (inventory.getStackInSlot(slotIndex) != null)
		{
			ItemStack itemStack = inventory.getStackInSlot(slotIndex).copy();

			if (this.filterItems.size() == 0 || this.isFiltering(itemStack))
			{
				itemStack.stackSize = ammount;
				inventory.decrStackSize(slotIndex, 1);
				return itemStack;
			}
		}

		return null;
	}

	/**
	 * is the item being restricted to a filter set
	 */
	public boolean isFiltering(ItemStack itemStack)
	{
		if (this.filterItems != null && itemStack != null)
		{
			for (int i = 0; i < filterItems.size(); i++)
			{
				if (filterItems.get(i) != null)
				{
					if (filterItems.get(i).isItemEqual(itemStack))
					{
						return !inverted;
					}
				}
			}
		}

		return inverted;
	}

}
