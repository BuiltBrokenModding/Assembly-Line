package assemblyline.common.machine.detector;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.prefab.BlockMachine;
import assemblyline.common.machine.filter.ItemFilter;

/**
 * Extend this block class if a filter is allowed to be placed inside of this block.
 * 
 * @author Calclavia
 */
public abstract class BlockFilterable extends BlockMachine
{
	public BlockFilterable(String name, int id, Material material, CreativeTabs creativeTab)
	{
		super(name, id, material, creativeTab);
	}

	/**
	 * Allows filters to be placed inside of this block.
	 */
	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TileEntityDetector)
			{
				ItemStack containingStack = ((TileEntityDetector) tileEntity).getStackInSlot(0);

				if (containingStack != null)
				{
					if (!world.isRemote)
					{
						EntityItem dropStack = new EntityItem(world, player.posX, player.posY, player.posZ, containingStack);
						dropStack.delayBeforeCanPickup = 0;
						world.spawnEntityInWorld(dropStack);
					}

					((TileEntityDetector) tileEntity).setInventorySlotContents(0, null);
					return true;
				}
				else
				{
					if (player.getCurrentEquippedItem() != null)
					{
						if (player.getCurrentEquippedItem().getItem() instanceof ItemFilter)
						{
							((TileEntityDetector) tileEntity).setInventorySlotContents(0, player.getCurrentEquippedItem());
							player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
							return true;
						}
					}
				}

			}
		}

		return false;
	}

}
