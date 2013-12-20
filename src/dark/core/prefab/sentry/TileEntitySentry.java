package dark.core.prefab.sentry;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

import com.dark.helpers.MathHelper;
import com.dark.helpers.RayTraceHelper;
import com.dark.network.PacketHandler;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;
import dark.api.ISentryGun;
import dark.core.prefab.entities.EntityTileDamage;
import dark.core.prefab.machine.TileEntityMachine;
import dark.machines.CoreMachine;

/** Prefab tileEntity for creating senty guns that can be of type aimed, mounted, or automated.
 * Contains most of the code for a sentry gun to operate short of aiming and operating logic. This
 * means the classes that extend this still need to tell the sentry were to aim. As well this
 * doesn't handle any firing events or damage events. This is only a shell for the sentry to be
 * created. Everything else is up to the sub classes of this class.
 * 
 * @author DarkGuardsman */
public abstract class TileEntitySentry extends TileEntityMachine implements ISentryGun
{
    protected EntityTileDamage entitySentry = null;
    protected TileEntityGunPlatform platform;
    protected ForgeDirection mountingSide = ForgeDirection.DOWN;

    protected boolean isAlive = true, isRunning = false, requiresPlatform = true;
    private float damage = 0.0f;
    private final float maxDamage;

    private Vector3 rotation = new Vector3(), targetRotation = new Vector3(), prevRotation = new Vector3();
    protected float roationSpeed = 10f, minPitch = -30, maxPitch = 30, minYaw = -180, maxYaw = 180, size = 1.0f;

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
        this.prevRotation = this.getRotation();
        this.rotation.x = MathHelper.clamp((float) MathHelper.updateRotation(this.rotation.x, this.targetRotation.x, this.roationSpeed), this.minPitch, this.maxPitch);
        this.rotation.y = MathHelper.clamp((float) MathHelper.updateRotation(this.rotation.y, this.targetRotation.y, this.roationSpeed), this.minYaw, this.maxYaw);
    }

    @Override
    public Vector3 getLook()
    {
        //TODO store this value so a new vector is not created each call
        return new Vector3(RayTraceHelper.getLook(this.getRotation().floatX(), this.getRotation().floatY(), this.size));
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
        if (this.targetRotation == null)
        {
            this.targetRotation = this.getRotation();
        }
        this.targetRotation.x += pitch;
        this.targetRotation.y += yaw;
        this.targetRotation.z += roll;
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
        if (source != null && ammount > 0)
        {
            if (source.equals(DamageSource.onFire))
            {
                //TODO cause heat damage slowly but not right away
                //TODO check for heat sources around the sentry
                //TODO mess with the sentries abilities when over heated
                return false;
            }
            else
            {
                this.setDamage(this.getDamage() - ammount);

                if (this.getDamage() <= 0)
                {
                    this.isAlive = false;
                    if (this.entitySentry != null)
                    {
                        this.entitySentry.setDead();
                    }
                    this.entitySentry = null;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAlive()
    {
        return this.isAlive;
    }

    @Override
    public float getDamage()
    {
        return this.damage;
    }

    @Override
    public void setDamage(float health)
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

    /* ******************************************************
     *  Save/Load/PacketHandling
     * ****************************************************** */

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (this.getRotation() != null)
        {
            nbt.setFloat("pitch", this.getRotation().floatX());
            nbt.setFloat("yaw", this.getRotation().floatY());
            nbt.setFloat("roll", this.getRotation().floatZ());
        }
        if (this.targetRotation != null)
        {
            nbt.setFloat("npitch", this.targetRotation.floatX());
            nbt.setFloat("nyaw", this.targetRotation.floatY());
            nbt.setFloat("nroll", this.targetRotation.floatZ());
        }
        nbt.setFloat("damage", this.getDamage());
        nbt.setByte("mountSide", (byte) this.mountingSide.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.getRotation().x = nbt.getFloat("pitch");
        this.getRotation().y = nbt.getFloat("yaw");
        this.getRotation().z = nbt.getFloat("roll");
        this.targetRotation = new Vector3();
        this.targetRotation.x = nbt.getFloat("pitch");
        this.targetRotation.y = nbt.getFloat("yaw");
        this.targetRotation.z = nbt.getFloat("roll");
        this.setDamage(nbt.getFloat("damage"));
        this.mountingSide = ForgeDirection.getOrientation(nbt.getByte("mountSide"));
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getTilePacket(CoreMachine.CHANNEL, this, "Desc", this.isRunning, this.rotation);
    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput dis, Player player)
    {
        try
        {
            if (this.worldObj.isRemote && !super.simplePacket(id, dis, player))
            {
                if (id.equalsIgnoreCase("Desc"))
                {
                    this.functioning = dis.readBoolean();
                    this.rotation = PacketHandler.readVector3(dis);
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
