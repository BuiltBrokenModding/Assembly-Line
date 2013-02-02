package assemblyline.common.block;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import assemblyline.common.AssemblyLine;
import assemblyline.common.TabAssemblyLine;

/**
 * A block that allows the placement of mass amount of a specific item within it. It will be allowed to go on Conveyor Belts
 * 
 * @author Calclavia
 * 
 */
public class BlockCrate extends BlockMachine
{
	public BlockCrate(int id, int texture)
	{
		super("crate", id, UniversalElectricity.machine);
		this.blockIndexInTexture = texture;
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
		this.setTextureFile(AssemblyLine.BLOCK_TEXTURE_PATH);
	}

	/**
	 * Placed the item the player is holding into the crate.
	 */
	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
			return true;
		if (world.getBlockTileEntity(x, y, z) instanceof TileEntityCrate)
		{
			TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);

			/**
			 * Make double clicking input all stacks.
			 */
			boolean allMode = false;

			if (world.getWorldTime() - tileEntity.prevClickTime < 10)
			{
				allMode = true;
			}

			tileEntity.prevClickTime = world.getWorldTime();
			// add items
			if (side == 1 || (side > 1 && hitY > 0.5))
			{
				if (allMode)
				{
					this.insertAllItems(tileEntity, player);
				}
				else
				{
					this.insertCurrentItem(tileEntity, player);
				}
			}
			// remove items
			else if (side == 0 || (side > 1 && hitY <= 0.5))
			{
				if (allMode)
				{
					this.ejectItems(tileEntity, player, TileEntityCrate.MAX_LIMIT);
				}
				else
				{
					ItemStack stack = tileEntity.getStackInSlot(0);
					if (stack != null)
					{
						this.ejectItems(tileEntity, player, stack.getMaxStackSize());
					}
				}
			}
		}

		return true;
	}

	@Override
	public int getRenderType()
	{
		return super.getRenderType();
	}

	/**
	 * Inserts a the itemStack the player is holding into the crate.
	 */
	public boolean insertCurrentItem(TileEntityCrate tileEntity, EntityPlayer player)
	{
		ItemStack currentStack = player.getCurrentEquippedItem();

		if (currentStack != null)
		{
			if (currentStack.isStackable())
			{
				player.inventory.setInventorySlotContents(player.inventory.currentItem, this.putIn(tileEntity, currentStack));
				return true;
			}// if the item being used is a create then try to merge the items inside
			else if (currentStack.getItem().itemID == AssemblyLine.blockCrate.blockID)
			{
				ItemStack containedStack = ItemBlockCrate.getContainingItemStack(currentStack);
				ItemStack crateStack = tileEntity.getStackInSlot(0);
				if (containedStack != null && (crateStack == null || (crateStack != null && containedStack.getItem().itemID == crateStack.getItem().itemID && containedStack.getItemDamage() == crateStack.getItemDamage())))
				{
					ItemStack returned = this.putIn(tileEntity, containedStack);
					ItemBlockCrate.setContainingItemStack(currentStack, returned );
					return true;
				}

			}
		}

		return false;
	}

	/**
	 * Inserts all items of the same type this player has into the crate.
	 * 
	 * @return
	 */
	public boolean insertAllItems(TileEntityCrate tileEntity, EntityPlayer player)
	{
		ItemStack requestStack = player.getCurrentEquippedItem();

		if (requestStack == null && tileEntity.getStackInSlot(0) != null)
		{
			requestStack = tileEntity.getStackInSlot(0).copy();
		}

		if (requestStack != null)
		{
			if (requestStack.isStackable())
			{
				for (int i = 0; i < player.inventory.getSizeInventory(); i++)
				{
					ItemStack currentStack = player.inventory.getStackInSlot(i);

					if (currentStack != null)
					{
						if (requestStack.isItemEqual(currentStack))
						{
							player.inventory.setInventorySlotContents(i, this.putIn(tileEntity, currentStack));
							if (player instanceof EntityPlayerMP)
							{
								((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
							}
						}
					}
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Ejects and item out of the crate and spawn it under the player entity.
	 * 
	 * @param tileEntity
	 * @param player
	 * @param maxStack - The maximum stack size to take out. Default should be 64.
	 * @return True on success
	 */
	public boolean ejectItems(TileEntityCrate tileEntity, EntityPlayer player, int maxStack)
	{
		World world = tileEntity.worldObj;
		ItemStack containingStack = tileEntity.getStackInSlot(0);

		if (containingStack != null)
		{
			if (containingStack.stackSize > 0)
			{
				int amountToTake = Math.min(containingStack.stackSize, maxStack);
				ItemStack dropStack = containingStack.copy();
				dropStack.stackSize = amountToTake;

				if (!world.isRemote)
				{
					EntityItem entityItem = new EntityItem(world, player.posX, player.posY, player.posZ, dropStack);

					float var13 = 0.05F;
					entityItem.motionX = ((float) world.rand.nextGaussian() * var13);
					entityItem.motionY = ((float) world.rand.nextGaussian() * var13 + 0.2F);
					entityItem.motionZ = ((float) world.rand.nextGaussian() * var13);
					entityItem.delayBeforeCanPickup = 0;
					world.spawnEntityInWorld(entityItem);
				}

				containingStack.stackSize -= amountToTake;
			}

			if (containingStack.stackSize <= 0)
			{
				containingStack = null;
			}

			tileEntity.setInventorySlotContents(0, containingStack);

			return true;
		}
		return false;
	}

	/**
	 * Puts an itemStack into the crate.
	 * 
	 * @param tileEntity
	 * @param itemStack
	 */
	private ItemStack putIn(TileEntityCrate tileEntity, ItemStack itemStack)
	{
		ItemStack containingStack = tileEntity.getStackInSlot(0);

		if (containingStack != null)
		{
			if (containingStack.isStackable() && containingStack.isItemEqual(itemStack))
			{
				int newStackSize = containingStack.stackSize + itemStack.stackSize;
				int overFlowAmount = newStackSize - tileEntity.getInventoryStackLimit();

				if (overFlowAmount > 0)
				{
					itemStack.stackSize = overFlowAmount;
				}
				else
				{
					itemStack.stackSize = 0;
				}

				containingStack.stackSize = newStackSize;
				tileEntity.setInventorySlotContents(0, containingStack);
			}
		}
		else
		{
			tileEntity.setInventorySlotContents(0, itemStack.copy());
			itemStack.stackSize = 0;
		}

		if (itemStack.stackSize <= 0) { return null; }

		return itemStack;
	}

	@Override
	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote && world.getBlockTileEntity(x, y, z) != null)
		{
			TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);
			ItemStack containingStack = tileEntity.getStackInSlot(0);

			if (containingStack != null)
			{
				if (containingStack.stackSize > 0)
				{
					float var6 = 0.7F;
					double var7 = (double) (world.rand.nextFloat() * var6) + (double) (1.0F - var6) * 0.5D;
					double var9 = (double) (world.rand.nextFloat() * var6) + (double) (1.0F - var6) * 0.5D;
					double var11 = (double) (world.rand.nextFloat() * var6) + (double) (1.0F - var6) * 0.5D;
					ItemStack dropStack = new ItemStack(this, 1);
					ItemBlockCrate.setContainingItemStack(dropStack, containingStack);
					EntityItem var13 = new EntityItem(world, (double) x + var7, (double) y + var9, (double) z + var11, dropStack);
					var13.delayBeforeCanPickup = 10;
					world.spawnEntityInWorld(var13);
					tileEntity.setInventorySlotContents(0, null);
					world.setBlockWithNotify(x, y, z, 0);

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityCrate();
	}

}
