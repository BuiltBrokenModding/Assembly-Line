package assemblyline.common.block;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		if (world.getBlockTileEntity(x, y, z) != null)
		{
			TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);
			ItemStack itemStack = par5EntityPlayer.getCurrentEquippedItem();

			if (itemStack != null)
			{
				if (tileEntity.containingItems[0] != null)
				{
					if (tileEntity.containingItems[0].isItemEqual(itemStack))
					{
						tileEntity.containingItems[0].stackSize += itemStack.stackSize;

						if (tileEntity.containingItems[0].stackSize > tileEntity.getInventoryStackLimit())
						{
							itemStack.stackSize = tileEntity.containingItems[0].stackSize - tileEntity.getInventoryStackLimit();
						}
						else
						{
							itemStack.stackSize = 0;
						}

						return true;
					}
				}
				else if (itemStack.isStackable())
				{
					tileEntity.containingItems[0] = itemStack;
					itemStack.stackSize = 0;
					return true;
				}

				if (itemStack.stackSize <= 0)
					par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, null);

			}
			else if (tileEntity.containingItems[0] != null)
			{
				int amountToTake = Math.min(tileEntity.containingItems[0].stackSize, 64);
				ItemStack newStack = tileEntity.containingItems[0].copy();
				newStack.stackSize = amountToTake;
				par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, newStack);
				tileEntity.containingItems[0].stackSize -= amountToTake;

				if (tileEntity.containingItems[0].stackSize <= 0)
				{
					tileEntity.containingItems[0] = null;
				}
			}
		}

		return false;
	}

	/**
	 * Drops the crate as a block that stores items within it.
	 */
	@Override
	public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (world.getBlockTileEntity(x, y, z) != null)
		{
			TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);

			if (tileEntity.containingItems[0] != null)
			{
				if (tileEntity.containingItems[0].stackSize > 0)
				{
					if (!world.isRemote)
					{
						float var6 = 0.7F;
						double var7 = (double) (world.rand.nextFloat() * var6) + (double) (1.0F - var6) * 0.5D;
						double var9 = (double) (world.rand.nextFloat() * var6) + (double) (1.0F - var6) * 0.5D;
						double var11 = (double) (world.rand.nextFloat() * var6) + (double) (1.0F - var6) * 0.5D;
						ItemStack dropStack = new ItemStack(this, 1);
						ItemBlockCrate.setContainingItemStack(dropStack, tileEntity.containingItems[0]);
						EntityItem var13 = new EntityItem(world, (double) x + var7, (double) y + var9, (double) z + var11, dropStack);
						var13.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(var13);
						tileEntity.containingItems[0] = null;
						world.setBlockWithNotify(x, y, z, 0);
					}
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
