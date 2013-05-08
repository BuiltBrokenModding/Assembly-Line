package assemblyline.common.armbot.command;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import universalelectricity.core.vector.Vector3;

/**
 * Used by arms to break a specific block in a position.
 * 
 * @author Calclavia
 */
public class CommandPlace extends Command
{
	int PLACE_TIME = 30;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		Vector3 serachPosition = this.tileEntity.getHandPosition();

		Block block = Block.blocksList[serachPosition.getBlockID(this.world)];

		if (block == null && ticks >= this.PLACE_TIME)
		{
			for (Entity entity : this.tileEntity.getGrabbedEntities())
			{
				if (entity instanceof EntityItem)
				{
					ItemStack itemStack = ((EntityItem) entity).getEntityItem();

					if (itemStack != null)
					{
						if (itemStack.getItem() instanceof ItemBlock)
						{
							((ItemBlock) itemStack.getItem()).placeBlockAt(itemStack, null, this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), 0, 0.5f, 0.5f, 0.5f, itemStack.getItemDamage());

							this.tileEntity.dropEntity(entity);
							return false;
						}
						else if (itemStack.getItem() instanceof IPlantable)
						{
							IPlantable plantable = ((IPlantable) itemStack.getItem());
							Block blockBelow = Block.blocksList[Vector3.add(serachPosition, new Vector3(0, -1, 0)).getBlockID(this.world)];

							if (blockBelow != null)
							{
								if (blockBelow.canSustainPlant(this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), ForgeDirection.UP, plantable))
								{
									int blockID = plantable.getPlantID(this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ());
									int blockMetadata = plantable.getPlantMetadata(this.world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ());

									if (this.world.setBlock(serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), blockID, blockMetadata, 3))
									{
										if (this.world.getBlockId(serachPosition.intX(), serachPosition.intY(), serachPosition.intZ()) == blockID)
										{
											Block.blocksList[blockID].onBlockPlacedBy(world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), null, itemStack);
											Block.blocksList[blockID].onPostBlockPlaced(world, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), blockMetadata);
											this.tileEntity.dropEntity(entity);
											return false;
										}
									}
								}
							}
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
