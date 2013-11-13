package dark.core.prefab.sentry;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import dark.api.ISentryGun;
import dark.core.prefab.EntityTileDamage;
import dark.core.prefab.machine.TileEntityMachine;

public abstract class TileEntitySentry extends TileEntityMachine implements ISentryGun
{
    protected EntityTileDamage entitySentry = null;
    protected TileEntityGunPlatform platform;
    protected ForgeDirection mountingSide = ForgeDirection.DOWN;

    protected boolean isAlive = true, isRunning = false, requiresPlatform = true;
    private float damage = 0.0f;
    private final float maxDamage;

    private Vector3 rotation = new Vector3(), newRotation = new Vector3(), prevRotation = new Vector3();

    public TileEntitySentry(float maxDamage)
    {
        this.maxDamage = maxDamage;
    }

    /* ******************************************************
     *  Logic code
     * ****************************************************** */
    @Override
    public void updateEntity()
    {
        super.updateEntity();        
        if (this.isFunctioning())
        {
            if (this.entitySentry == null)
            {
                this.getDamageEntity(true);
            }
            this.updateRotation();
        }
    }

    @Override
    public boolean canFunction()
    {
        return super.canFunction() && this.isAlive() && (!this.requiresPlatform || this.requiresPlatform && this.getPlatform() != null);
    }

    /* ******************************************************
     *  Sentry code
     * ****************************************************** */

    @Override
    public SentryType getType()
    {
        return SentryType.AIMED;
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

    /* ******************************************************
     *  Rotation code
     * ****************************************************** */
    public void updateRotation()
    {

    }

    @Override
    public Vector3 getLook()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3 getRotation()
    {
        if (this.rotation == null)
        {
            this.rotation = new Vector3();
        }
        return rotation;
    }

    @Override
    public void updateRotation(float pitch, float yaw, float roll)
    {
        if (this.newRotation == null)
        {
            this.newRotation = this.getRotation();
        }
        this.newRotation.x += pitch;
        this.newRotation.y += yaw;
        this.newRotation.z += roll;
    }

    @Override
    public void setRotation(float pitch, float yaw, float roll)
    {
        this.getRotation().x = pitch;
        this.getRotation().y = yaw;
        this.getRotation().z = roll;
    }

    /* ******************************************************
     *  Damage Code
     * ****************************************************** */

    @Override
    public boolean onDamageTaken(DamageSource source, float ammount)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isAlive()
    {
        return this.isAlive;
    }

    @Override
    public float health()
    {
        return this.damage;
    }

    @Override
    public void setHealth(float health)
    {
        this.damage = health;
    }

    @Override
    public float getMaxHealth()
    {

        return this.maxDamage;
    }

    @Override
    public boolean canApplyPotion(PotionEffect par1PotionEffect)
    {
        return false;
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

    /* ******************************************************
     *  Player interaction
     * ****************************************************** */

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(EntityPlayer player, int keycode)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
