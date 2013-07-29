package dark.assembly.common.machine;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.assembly.api.IBelt;
import dark.assembly.common.imprinter.prefab.TileEntityFilterable;

/** @author Darkguardsman */
public class TileEntityRejector extends TileEntityFilterable
{

    /** should the piston fire, or be extended */
    public boolean firePiston = false;

    public TileEntityRejector()
    {
        super(1);
    }

    @Override
    public void onUpdate()
    {
        /** Has to update a bit faster than a conveyer belt */
        if (this.ticks % 5 == 0 && !this.isDisabled())
        {
            this.firePiston = false;

            Vector3 searchPosition = new Vector3(this);
            searchPosition.modifyPositionFromSide(this.getDirection());
            TileEntity tileEntity = searchPosition.getTileEntity(this.worldObj);

            try
            {
                if (this.isRunning())
                {
                    /** Find all entities in the position in which this block is facing and attempt
                     * to push it out of the way. */
                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(searchPosition.x, searchPosition.y, searchPosition.z, searchPosition.x + 1, searchPosition.y + 1, searchPosition.z + 1);
                    List<Entity> entitiesInFront = this.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

                    for (Entity entity : entitiesInFront)
                    {
                        if (this.canEntityBeThrow(entity))
                        {
                            this.throwItem(tileEntity, this.getDirection(), entity);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /** Pushs an entity in the direction in which the rejector is facing */
    public void throwItem(TileEntity tileEntity, ForgeDirection side, Entity entity)
    {
        this.firePiston = true;
        //TODO add config to adjust the motion magnitude per rejector
        entity.motionX = (double) side.offsetX * 0.1;
        entity.motionY += 0.10000000298023224D;
        entity.motionZ = (double) side.offsetZ * 0.1;

        if (!this.worldObj.isRemote && tileEntity instanceof IBelt)
        {
            ((IBelt) tileEntity).ignoreEntity(entity);
        }
    }

    /** Checks to see if the rejector can push the entity in the facing direction */
    public boolean canEntityBeThrow(Entity entity)
    {
        // TODO Add other things than items
        if (entity instanceof EntityItem)
        {
            EntityItem entityItem = (EntityItem) entity;
            ItemStack itemStack = entityItem.getEntityItem();

            return this.isFiltering(itemStack);
        }

        return false;
    }

    @Override
    public boolean canConnect(ForgeDirection dir)
    {
        return dir != this.getDirection();
    }
}
