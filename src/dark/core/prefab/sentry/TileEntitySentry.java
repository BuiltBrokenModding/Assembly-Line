package dark.core.prefab.sentry;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import dark.api.ISentryGun;
import dark.core.prefab.EntityTileDamage;

public class TileEntitySentry extends TileEntityAdvanced implements ISentryGun
{
    protected EntityTileDamage entitySentry = null;
    protected TileEntityGunPlatform platform;
    protected ForgeDirection mountingSide = ForgeDirection.DOWN;
    protected boolean isAlive = true;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (this.isAlive && this.entitySentry == null)
        {
            this.getDamageEntity(true);
        }
    }

    @Override
    public SentryType getType()
    {
        return SentryType.AIMED;
    }

    public EntityTileDamage getDamageEntity()
    {
        return this.getDamageEntity(isAlive);
    }

    public EntityTileDamage getDamageEntity(boolean create)
    {
        if (entitySentry == null || entitySentry.isDead && create)
        {
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
            List<EntityTileDamage> list = this.worldObj.getEntitiesWithinAABB(EntityTileDamage.class, box);
            for (EntityTileDamage entity : list)
            {
                if (!entity.isDead && (entity.host == null || entity.host == this))
                {
                    entity.host = this;
                    this.entitySentry = entity;
                }
                else
                {
                    this.entitySentry = new EntityTileDamage(this);
                }
            }
        }
        return entitySentry;
    }

    @Override
    public TileEntity getPlatform()
    {
        Vector3 mountVec = new Vector3(this).modifyPositionFromSide(mountingSide);
        if (platform == null || platform.isInvalid() || !new Vector3(platform).equals(mountVec))
        {
            TileEntity entity = mountVec.getTileEntity(this.worldObj);
            if (entity instanceof TileEntityGunPlatform)
            {
                this.platform = (TileEntityGunPlatform) entity;
            }
        }
        return platform;
    }
}
