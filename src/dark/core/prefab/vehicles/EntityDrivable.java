package dark.core.prefab.vehicles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import dark.core.interfaces.IControlReceiver;
import dark.core.prefab.EntityAdvanced;

public class EntityDrivable extends EntityAdvanced implements IControlReceiver
{
    public double speed = 0.0D, maxSpeed = 0.5D;

    public double boatX, boatY, boatZ, boatYaw, boatPitch;
    public int boatPosRotationIncrements;

    public EntityDrivable(World world)
    {
        super(world);
        this.setSize(0.98F, 0.7F);
        this.preventEntitySpawning = true;
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
    }

    public EntityDrivable(World world, double xx, double yy, double zz)
    {
        this(world);
        this.setPosition(xx, yy + this.yOffset, zz);
    }

    @Override
    public boolean keyTyped(EntityPlayer player, char character, int keycode)
    {
        //TODO lay out WASD keys as well as R & F for auto move
        return false;
    }

    @Override
    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            //Changes the player's position based on the boats rotation
            double deltaX = Math.cos(this.rotationYaw * Math.PI / 180.0D + 114.8) * -0.5D;
            double deltaZ = Math.sin(this.rotationYaw * Math.PI / 180.0D + 114.8) * -0.5D;
            this.riddenByEntity.setPosition(this.posX + deltaX, this.posY - 2 + this.riddenByEntity.getYOffset(), this.posZ + deltaZ);
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.worldObj.isRemote)
        {
            this.worldObj.spawnParticle("mobSpell", this.posX, this.posY, this.posZ, 0, 0, 0);

        }

        if (this.isCollidedHorizontally)
        {
            this.motionY = 0.1D;
        }

        this.applyFrication();

        this.motionX = -(this.speed * Math.cos((this.rotationYaw - 90F) * Math.PI / 180.0D));
        this.motionZ = -(this.speed * Math.sin((this.rotationYaw - 90F) * Math.PI / 180.0D));
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.speed > this.maxSpeed)
        {
            this.speed = this.maxSpeed;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    /** By default this slows the vehicle down with a constant. However this can be used to apply
     * advanced friction based on materials */
    public void applyFrication()
    {
        if (this.inWater)
        {
            this.speed *= 0.8D;
        }
        if (this.isCollidedHorizontally)
        {
            this.speed *= 0.9;
        }
        else
        {
            this.speed *= 0.98D;
            this.motionY -= 0.04D;
        }

    }

    @Override
    public void setPositionAndRotation2(double d, double d1, double d2, float f, float f1, int i)
    {
        if (this.riddenByEntity != null)
        {
            if (this.riddenByEntity instanceof EntityPlayer && FMLClientHandler.instance().getClient().thePlayer.equals(this.riddenByEntity))
            {
            }
            else
            {
                this.boatPosRotationIncrements = i + 5;
                this.boatX = d;
                this.boatY = d1 + (this.riddenByEntity == null ? 1 : 0);
                this.boatZ = d2;
                this.boatYaw = f;
                this.boatPitch = f1;
            }
        }
    }

    @Override
    public void performHurtAnimation()
    {
        this.setHealth(this.getHealth() + this.getHealth() * 10.0F);
    }

    @Override
    public boolean attackEntityFrom(DamageSource var1, float var2)
    {
        if (this.isDead || var1.equals(DamageSource.cactus))
        {
            return true;
        }
        else
        {
            this.setBeenAttacked();
            this.setHealth(this.getHealth() + this.getHealth() * 10.0F);
            this.setBeenAttacked();

            if (var1.getEntity() instanceof EntityPlayer && ((EntityPlayer) var1.getEntity()).capabilities.isCreativeMode)
            {
                this.setHealth(100);
            }

            if (this.getHealth() > this.getMaxHealth())
            {
                if (this.riddenByEntity != null)
                {
                    this.riddenByEntity.mountEntity(this);
                }

                if (!this.worldObj.isRemote)
                {
                    if (this.riddenByEntity != null)
                    {
                        this.riddenByEntity.mountEntity(this);
                    }
                }
                //TODO set vehicle in a unusable state rather than destroy it like a boat

            }

            return true;
        }
    }

    /** Checks if the vehicle can move, use this to check for fuel */
    public boolean canMove()
    {
        return true;
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public double getMountedYOffset()
    {
        return -.3;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
    }

    @Override
    public boolean interactFirst(EntityPlayer var1)
    {
        if (this.worldObj.isRemote)
        {
            return true;
        }
        else
        {
            if (this.riddenByEntity != null)
            {
                var1.mountEntity(null);
                return true;
            }
            else
            {
                var1.mountEntity(this);
                return true;
            }
        }
    }

}
