package assemblyline.common.machine.command;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import universalelectricity.core.vector.Vector3;

/**
 * Used by arms to break a specific block in a position.
 * 
 * @author Calclavia
 */
public class CommandPlace extends Command
{
	@Override
	protected boolean doTask()
	{
		super.doTask();

		Vector3 serachPosition = this.tileEntity.getHandPosition();

		Block block = Block.blocksList[serachPosition.getBlockID(this.world)];

		if (block == null)
		{
			for (Entity entity : this.tileEntity.grabbedEntities)
			{
				if (entity instanceof EntityItem)
				{
					ItemStack itemStack = ((EntityItem) entity).getEntityItem();

					if (itemStack != null)
					{
						if (itemStack.getItem() instanceof ItemBlock)
						{
							((ItemBlock) itemStack.getItem()).placeBlockAt(itemStack, null, this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), 0, 0.5f, 0.5f, 0.5f, itemStack.getItemDamage());

							this.tileEntity.grabbedEntities.remove(entity);
							return false;
						}
						else if (itemStack.getItem() instanceof IPlantable)
						{
							IPlantable plantable = ((IPlantable) itemStack.getItem());
							int blockID = plantable.getPlantID(this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ());
							int blockMetadata = plantable.getPlantMetadata(this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ());

							if (!world.setBlockAndMetadataWithNotify(serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), blockID, blockMetadata)) { return false; }

							if (world.getBlockId(serachPosition.intX(), serachPosition.intY(), serachPosition.intZ()) == blockID)
							{
								Block.blocksList[blockID].onBlockPlacedBy(world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), null);
								Block.blocksList[blockID].onPostBlockPlaced(world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), blockMetadata);
							}

							this.tileEntity.grabbedEntities.remove(entity);
							return false;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public String toString()
	{
		return "PLACE";
	}
}
