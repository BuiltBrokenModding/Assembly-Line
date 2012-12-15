package assemblyline.common.block;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.UETab;

/**
 * A block that allows the placement of mass amount of a specific item within it. It will be allowed
 * to go on Conveyor Belts
 * 
 * @author Calclavia
 * 
 */
public class BlockCrate extends BlockMachine
{
	public BlockCrate(int par1)
	{
		super("crate", par1, UniversalElectricity.machine);
		this.blockIndexInTexture = Block.blockSteel.blockIndexInTexture;
		this.setCreativeTab(UETab.INSTANCE);
	}

	/**
	 * Placed the item the player is holding into the crate.
	 */
	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		if (!world.isRemote && world.getBlockTileEntity(x, y, z) != null)
		{
			TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);
			ItemStack containingStack = tileEntity.getStackInSlot(0);
			ItemStack itemStack = par5EntityPlayer.getCurrentEquippedItem();

			if (itemStack != null)
			{
				if (itemStack.isStackable())
				{
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

					if (itemStack.stackSize <= 0)
					{
						par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, null);
					}
				}
			}
		}

		return true;
	}

	/**
	 * Drops the crate as a block that stores items within it.
	 */
	@Override
	public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote && world.getBlockTileEntity(x, y, z) != null)
		{
			TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);
			ItemStack containingStack = tileEntity.getStackInSlot(0);

			if (containingStack != null)
			{
				int amountToTake = Math.min(containingStack.stackSize, 64);
				ItemStack dropStack = containingStack.copy();
				dropStack.stackSize = amountToTake;

				EntityItem entityItem = new EntityItem(world, par5EntityPlayer.posX, par5EntityPlayer.posY, par5EntityPlayer.posZ, dropStack);

				float var13 = 0.05F;
				entityItem.motionX = ((float) world.rand.nextGaussian() * var13);
				entityItem.motionY = ((float) world.rand.nextGaussian() * var13 + 0.2F);
				entityItem.motionZ = ((float) world.rand.nextGaussian() * var13);
				entityItem.delayBeforeCanPickup = 0;
				world.spawnEntityInWorld(entityItem);

				containingStack.stackSize -= amountToTake;

				if (containingStack.stackSize <= 0)
				{
					containingStack = null;
				}

				tileEntity.setInventorySlotContents(0, containingStack);
			}
		}
		return true;
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
