package dark.assembly.common.armbot.command;

import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbotTask.TaskType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import universalelectricity.core.vector.Vector3;

/** Used by arms to break a specific block in a position.
 *
 * @author Calclavia */
public class CommandPlace extends Command
{
    int PLACE_TIME = 30;

    public CommandPlace()
    {
        super("Place", TaskType.DEFINEDPROCESS);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    protected boolean onUpdate()
    {
        super.onUpdate();

        Vector3 serachPosition = this.tileEntity.getHandPosition();

        Block block = Block.blocksList[serachPosition.getBlockID(this.worldObj)];

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
                            ((ItemBlock) itemStack.getItem()).placeBlockAt(itemStack, null, this.worldObj, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), 0, 0.5f, 0.5f, 0.5f, itemStack.getItemDamage());

                            this.tileEntity.drop(entity);
                            return false;
                        }
                        else if (itemStack.getItem() instanceof IPlantable)
                        {
                            IPlantable plantable = ((IPlantable) itemStack.getItem());
                            Block blockBelow = Block.blocksList[Vector3.add(serachPosition, new Vector3(0, -1, 0)).getBlockID(this.worldObj)];

                            if (blockBelow != null)
                            {
                                if (blockBelow.canSustainPlant(this.worldObj, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), ForgeDirection.UP, plantable))
                                {
                                    int blockID = plantable.getPlantID(this.worldObj, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ());
                                    int blockMetadata = plantable.getPlantMetadata(this.worldObj, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ());

                                    if (this.worldObj.setBlock(serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), blockID, blockMetadata, 3))
                                    {
                                        if (this.worldObj.getBlockId(serachPosition.intX(), serachPosition.intY(), serachPosition.intZ()) == blockID)
                                        {
                                            Block.blocksList[blockID].onBlockPlacedBy(worldObj, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), null, itemStack);
                                            Block.blocksList[blockID].onPostBlockPlaced(worldObj, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), blockMetadata);
                                            this.tileEntity.drop(entity);
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
