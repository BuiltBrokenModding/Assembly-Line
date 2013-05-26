package assemblyline.common.block;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import assemblyline.common.AssemblyLine;
import assemblyline.common.PathfinderCrate;
import assemblyline.common.TabAssemblyLine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A block that allows the placement of mass amount of a specific item within it. It will be allowed
 * to go on Conveyor Belts
 * 
 * @author Calclavia
 * 
 */
public class BlockCrate extends BlockALMachine
{
	Icon crate_icon;

	public BlockCrate(int id, int texture)
	{
		super(id, UniversalElectricity.machine);
		this.setUnlocalizedName("crate");
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg)
	{
		this.crate_icon = iconReg.registerIcon(AssemblyLine.TEXTURE_NAME_PREFIX + "crate");
	}

	@Override
	public Icon getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z, int side)
	{
		return this.crate_icon;
	}

	@Override
	public Icon getIcon(int side, int metadata)
	{
		return this.crate_icon;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (world.getBlockTileEntity(x, y, z) instanceof TileEntityCrate)
			{
				TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);

				/**
				 * Make double clicking input all stacks.
				 */
				boolean allMode = (world.getWorldTime() - tileEntity.prevClickTime < 10);

				tileEntity.prevClickTime = world.getWorldTime();

				this.tryEject(tileEntity, player, allMode);
			}

		}
	}

	/**
	 * Placed the item the player is holding into the crate.
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (super.onBlockActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ))
		{
			return true;
		}

		if (!world.isRemote)
		{
			if (world.getBlockTileEntity(x, y, z) instanceof TileEntityCrate)
			{
				TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);

				/**
				 * Make double clicking input all stacks.
				 */
				boolean allMode = (world.getWorldTime() - tileEntity.prevClickTime < 10);

				tileEntity.prevClickTime = world.getWorldTime();
				ItemStack current = entityPlayer.inventory.getCurrentItem();
				if (side == 1 && entityPlayer.capabilities.isCreativeMode)
				{
					if (current != null && tileEntity.getSampleStack() == null)
					{
						BlockCrate.addStackToCrate(tileEntity, new ItemStack(current.itemID, TileEntityCrate.getSlotCount(world.getBlockMetadata(x, y, z)) * 64, current.getItemDamage()));
					}
				}
				// Add items
				if (side == 1 || (side > 1 && hitY > 0.5) || !entityPlayer.capabilities.isCreativeMode)
				{
					this.tryInsert(tileEntity, entityPlayer, allMode);
				}
				// Remove items
				else if (side == 0 || (side > 1 && hitY <= 0.5))
				{
					this.tryEject(tileEntity, entityPlayer, allMode);
				}

			}
		}

		return true;
	}

	/**
	 * Try to inject it into the crate. Otherwise, look around for nearby crates and try to put them
	 * in.
	 */
	public void tryInsert(TileEntityCrate tileEntity, EntityPlayer player, boolean allMode, boolean doSearch)
	{
		boolean success;

		if (allMode)
		{
			success = this.insertAllItems(tileEntity, player);
		}
		else
		{
			success = this.insertCurrentItem(tileEntity, player);
		}

		if (!success && doSearch)
		{
			PathfinderCrate pathfinder = new PathfinderCrate().init(tileEntity);

			for (TileEntity checkTile : pathfinder.iteratedNodes)
			{
				if (checkTile instanceof TileEntityCrate)
				{
					AssemblyLine.blockCrate.tryInsert(((TileEntityCrate) checkTile), player, allMode, false);
				}
			}
		}
	}

	public void tryInsert(TileEntityCrate tileEntity, EntityPlayer player, boolean allMode)
	{
		this.tryInsert(tileEntity, player, allMode, true);
	}

	public void tryEject(TileEntityCrate tileEntity, EntityPlayer player, boolean allMode)
	{
		if (tileEntity.getSampleStack() == null)
		{
			return;
		}
		if (allMode && !player.isSneaking())
		{
			this.ejectItems(tileEntity, player, tileEntity.getSlotCount() * 64);
		}
		else
		{
			if (player.isSneaking())
			{
				this.ejectItems(tileEntity, player, 1);
			}
			else
			{
				this.ejectItems(tileEntity, player, tileEntity.getSampleStack().getMaxStackSize());
			}
		}
	}

	/**
	 * Inserts a the itemStack the player is holding into the crate.
	 */
	public boolean insertCurrentItem(TileEntityCrate tileEntity, EntityPlayer player)
	{
		ItemStack currentStack = player.getCurrentEquippedItem();

		if (currentStack != null)
		{
			if (currentStack.getItem().itemID == AssemblyLine.blockCrate.blockID)
			{
				ItemStack containedStack = ItemBlockCrate.getContainingItemStack(currentStack);
				ItemStack crateStack = tileEntity.getSampleStack();
				if (containedStack != null && (crateStack == null || (crateStack != null && containedStack.isItemEqual(crateStack))))
				{
					ItemStack returned = BlockCrate.addStackToCrate(tileEntity, containedStack);
					ItemBlockCrate.setContainingItemStack(currentStack, returned);
					return true;
				}

			}
			else
			{
				if (tileEntity.getSampleStack() != null)
				{
					if (!tileEntity.getSampleStack().isItemEqual(currentStack))
					{
						return false;
					}
				}

				player.inventory.setInventorySlotContents(player.inventory.currentItem, BlockCrate.addStackToCrate(tileEntity, currentStack));
				return true;
			}
		}

		return false;
	}

	/**
	 * Inserts all items of the same type this player has into the crate.
	 * 
	 * @return True on success
	 */
	public boolean insertAllItems(TileEntityCrate tileEntity, EntityPlayer player)
	{
		ItemStack requestStack = null;

		if (tileEntity.getSampleStack() != null)
		{
			requestStack = tileEntity.getSampleStack().copy();
		}

		if (requestStack == null)
		{
			requestStack = player.getCurrentEquippedItem();
		}

		if (requestStack != null)
		{
			boolean success = false;

			for (int i = 0; i < player.inventory.getSizeInventory(); i++)
			{
				ItemStack currentStack = player.inventory.getStackInSlot(i);

				if (currentStack != null)
				{
					if (requestStack.isItemEqual(currentStack))
					{
						player.inventory.setInventorySlotContents(i, BlockCrate.addStackToCrate(tileEntity, currentStack));

						if (player instanceof EntityPlayerMP)
						{
							((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
						}

						success = true;
					}
				}
			}
			return success;
		}
		return false;
	}

	/**
	 * Ejects and item out of the crate and spawn it under the player entity.
	 * 
	 * @param tileEntity
	 * @param player
	 * @param requestSize - The maximum stack size to take out. Default should be 64.
	 * @return True on success
	 */
	public boolean ejectItems(TileEntityCrate tileEntity, EntityPlayer player, int requestSize)
	{
		World world = tileEntity.worldObj;
		if (!world.isRemote)
		{
			ItemStack sampleStack = tileEntity.getSampleStack();
			int ammountEjected = 0;
			if (sampleStack != null && requestSize > 0)
			{
				for (int slot = 0; slot < tileEntity.getSizeInventory(); slot++)
				{
					ItemStack slotStack = tileEntity.getStackInSlot(slot);

					if (slotStack != null && slotStack.stackSize > 0)
					{
						int amountToTake = Math.min(slotStack.stackSize, requestSize);

						ItemStack dropStack = slotStack.copy();
						dropStack.stackSize = amountToTake;

						EntityItem entityItem = new EntityItem(world, player.posX, player.posY, player.posZ, dropStack);
						entityItem.delayBeforeCanPickup = 0;
						world.spawnEntityInWorld(entityItem);

						slotStack.stackSize -= amountToTake;
						ammountEjected += amountToTake;
						if (slotStack.stackSize <= 0)
						{
							slotStack = null;
						}
						tileEntity.setInventorySlotContents(slot, slotStack);

					}
					if (ammountEjected >= requestSize)
					{
						return true;
					}
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Puts an itemStack into the crate.
	 * 
	 * @param tileEntity
	 * @param itemStack
	 */
	public static ItemStack addStackToCrate(TileEntityCrate tileEntity, ItemStack itemStack)
	{
		if (itemStack == null || itemStack.getItem().isDamageable() && itemStack.getItem().getItemDamageFromStack(itemStack) > 0)
		{
			return itemStack;
		}

		ItemStack containingStack = tileEntity.getSampleStack();

		if (containingStack == null || containingStack != null && containingStack.isItemEqual(itemStack))
		{
			int room = (tileEntity.getSizeInventory() * 64) - (containingStack != null ? containingStack.stackSize : 0);
			if (itemStack.stackSize <= room)
			{
				tileEntity.addToStack(itemStack);
				itemStack = null;
			}
			else
			{
				tileEntity.addToStack(itemStack, room);
				itemStack.stackSize -= room;
			}
			return itemStack;

		}

		if (itemStack.stackSize <= 0)
		{
			return null;
		}

		return itemStack;
	}

	@Override
	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote && world.getBlockTileEntity(x, y, z) != null)
		{
			TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);
			ItemStack containingStack = tileEntity.getSampleStack();
			tileEntity.buildSampleStack(false);

			if (containingStack != null)
			{
				if (containingStack.stackSize > 0)
				{
					float area = 0.7F;
					double dropX = (double) (world.rand.nextFloat() * area) + (double) (1.0F - area) * 0.5D;
					double dropY = (double) (world.rand.nextFloat() * area) + (double) (1.0F - area) * 0.5D;
					double dropZ = (double) (world.rand.nextFloat() * area) + (double) (1.0F - area) * 0.5D;

					ItemStack dropStack = new ItemStack(this, 1, tileEntity.getTier());
					ItemBlockCrate.setContainingItemStack(dropStack, containingStack);

					EntityItem var13 = new EntityItem(world, (double) x + dropX, (double) y + dropY, (double) z + dropZ, dropStack);
					var13.delayBeforeCanPickup = 10;
					world.spawnEntityInWorld(var13);

					for (int i = 0; i < tileEntity.getSizeInventory(); i++)
					{
						tileEntity.setInventorySlotContents(i, null);
					}
					world.setBlock(x, y, z, 0, 0, 3);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata;
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityCrate();
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List list)
	{
		for (int i = 0; i < 3; i++)
		{
			list.add(new ItemStack(this, 1, i));
		}
		try
		{
			ItemStack stack = new ItemStack(this);
			ItemBlockCrate.setContainingItemStack(stack, new ItemStack(1, 2048, 0));
			list.add(stack);
			// 3903
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
