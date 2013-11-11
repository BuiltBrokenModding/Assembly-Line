package dark.core.prefab.vehicles;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.Player;
import dark.core.common.CoreRecipeLoader;
import dark.core.helpers.MathHelper;
import dark.core.interfaces.IControlReceiver;
import dark.core.network.ISimplePacketReceiver;
import dark.core.network.PacketManagerEntity;
import dark.core.network.PacketManagerKeyEvent;
import dark.core.prefab.EntityAdvanced;

public abstract class EntityVehicle extends EntityAdvanced implements IControlReceiver, ISimplePacketReceiver
{
    //1m/tick is 80km/h or 50mi/h
    //0.5/tick is 40km/h
    public double speed = 0.0, maxSpeed = 0.32, turnRate = 3, acceration = .1;

    public double boatX, boatY, boatZ, boatYaw, boatPitch;
    public int boatPosRotationIncrements;

    public EntityVehicle(World world)
    {
        super(world);
        this.setSize(0.98F, 0.7F);
        this.preventEntitySpawning = true;
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        this.yOffset = 0.45f;
        PacketManagerKeyEvent.instance().register(this);
    }

    public EntityVehicle(World world, double xx, double yy, double zz)
    {
        this(world);
        this.setPosition(xx, yy + this.yOffset, zz);
    }

    @Override
    public boolean keyTyped(EntityPlayer player, int keycode)
    {
        System.out.println("Key: " + keycode + "  P: " + (player != null ? player.username : "null"));
        if (player != null && this.riddenByEntity instanceof EntityPlayer && ((EntityPlayer) this.riddenByEntity).username.equalsIgnoreCase(player.username))
        {
            boolean flag = false;
            //TODO add auto forward and backwards keys like those in WoT
            if (keycode == Minecraft.getMinecraft().gameSettings.keyBindForward.keyCode)
            {
                this.accelerate(true);
            }
            if (keycode == Minecraft.getMinecraft().gameSettings.keyBindBack.keyCode)
            {
                this.accelerate(false);
            }
            if (keycode == Minecraft.getMinecraft().gameSettings.keyBindLeft.keyCode)
            {
                this.turn(true);
            }
            if (keycode == Minecraft.getMinecraft().gameSettings.keyBindRight.keyCode)
            {
                this.turn(false);
            }
            //Power brakes
            if (keycode == Minecraft.getMinecraft().gameSettings.keyBindJump.keyCode)
            {
                this.speed -= 2.f;
                if (speed <= 0)
                {
                    speed = 0;
                }
            }
            return flag;
        }
        return false;
    }

    @Override
    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            //Changes the player's position based on the boats rotation
            double deltaX = Math.cos(this.rotationYaw * Math.PI / 180.0D + 114.8) * -0.23D;
            double deltaZ = Math.sin(this.rotationYaw * Math.PI / 180.0D + 114.8) * -0.23D;
            this.riddenByEntity.setPosition(this.posX + deltaX, this.posY + this.riddenByEntity.getYOffset(), this.posZ + deltaZ);

            if (this.riddenByEntity.rotationYaw > this.rotationYaw + 30)
            {
                this.riddenByEntity.rotationYaw = this.rotationYaw + 30;
            }
            if (this.riddenByEntity.rotationYaw < this.rotationYaw - 30)
            {
                this.riddenByEntity.rotationYaw = this.rotationYaw - 30;
            }
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.applyFriction();
        if (this.worldObj.isRemote)
        {
            this.worldObj.spawnParticle("mobSpell", this.posX, this.posY, this.posZ, 0, 0, 0);
        }

        if (this.worldObj.isRemote && (this.riddenByEntity == null || !(this.riddenByEntity instanceof EntityPlayer) || !FMLClientHandler.instance().getClient().thePlayer.equals(this.riddenByEntity)))
        {
            double x, y, z;
            if (this.boatPosRotationIncrements > 0)
            {
                x = this.posX + (this.boatX - this.posX) / this.boatPosRotationIncrements;
                y = this.posY + (this.boatY - this.posY) / this.boatPosRotationIncrements;
                z = this.posZ + (this.boatZ - this.posZ) / this.boatPosRotationIncrements;

                this.rotationYaw = (float) (this.rotationYaw + MathHelper.wrapAngleTo180_double(this.boatYaw - this.rotationYaw) / this.boatPosRotationIncrements);
                this.rotationPitch = (float) (this.rotationPitch + (this.boatPitch - this.rotationPitch) / this.boatPosRotationIncrements);
                --this.boatPosRotationIncrements;
                this.setPosition(x, y, z);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                x = this.posX + this.motionX;
                y = this.posY + this.motionY;
                z = this.posZ + this.motionZ;
                if (this.riddenByEntity != null)
                {
                    this.setPosition(x, y, z);
                }

                if (this.onGround)
                {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }
            return;
        }

        if (this.isCollidedHorizontally)
        {
            this.speed *= 0.9;
            this.motionY = 0.1D;
        }

        if (this.worldObj.isRemote)
        {
            if (this.canMove())
            {
                this.motionX = -(this.speed * Math.cos((this.rotationYaw - 90F) * Math.PI / 180.0D));
                this.motionZ = -(this.speed * Math.sin((this.rotationYaw - 90F) * Math.PI / 180.0D));
            }
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
        }

        if (this.speed > this.maxSpeed)
        {
            this.speed = this.maxSpeed;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (ticks % 5 == 0)
        {
            if (this.worldObj.isRemote && Minecraft.getMinecraft().thePlayer == this.riddenByEntity)
            {
                PacketManagerEntity.sendEntityUpdatePacket(this, true, "Desc", this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.motionX, this.motionY, this.motionZ);
            }
            else
            {
                this.updateClients();
            }
        }

    }

    /** Called to update all the clients with new information */
    public abstract void updateClients();

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput data, Player player)
    {
        if (id.equalsIgnoreCase("Desc") && player == this.riddenByEntity)
        {
            this.setPositionAndRotation(data.readDouble(), data.readDouble(), data.readDouble(), data.readFloat(), data.readFloat());
            this.motionX = data.readDouble();
            this.motionY = data.readDouble();
            this.motionZ = data.readDouble();
            return true;
        }
        return false;
    }

    public void checkCollisions()
    {
        if (!this.worldObj.isRemote)
        {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
            int l;

            if (list != null && !list.isEmpty())
            {
                for (l = 0; l < list.size(); ++l)
                {
                    Entity entity = (Entity) list.get(l);

                    if (entity != this.riddenByEntity && entity.canBePushed())
                    {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            for (l = 0; l < 4; ++l)
            {
                int i1 = MathHelper.floor_double(this.posX + ((double) (l % 2) - 0.5D) * 0.8D);
                int j1 = MathHelper.floor_double(this.posZ + ((double) (l / 2) - 0.5D) * 0.8D);

                for (int k1 = 0; k1 < 2; ++k1)
                {
                    int l1 = MathHelper.floor_double(this.posY) + k1;
                    int i2 = this.worldObj.getBlockId(i1, l1, j1);

                    if (i2 == Block.snow.blockID)
                    {
                        this.worldObj.setBlockToAir(i1, l1, j1);
                    }
                    else if (i2 == Block.waterlily.blockID)
                    {
                        this.worldObj.destroyBlock(i1, l1, j1, true);
                    }
                }
            }
        }
    }

    /** Increases the speed by a determined amount per tick the player holds the forward key down
     * 
     * @param forward */
    public void accelerate(boolean forward)
    {
        if (forward)
        {
            this.speed += this.acceration;
            if (this.speed > this.maxSpeed)
            {
                this.speed = this.maxSpeed;
            }

        }
        else
        {
            this.speed -= this.acceration;
            if (this.speed < -this.maxSpeed)
            {
                this.speed = -this.maxSpeed;
            }

        }
    }

    public void turn(boolean left)
    {
        if (left)
        {
            this.rotationYaw -= this.turnRate;
        }
        else
        {
            this.rotationYaw += this.turnRate;
        }
    }

    /** By default this slows the vehicle down with a constant. However this can be used to apply
     * advanced friction based on materials */
    public void applyFriction()
    {
        this.motionY -= 0.03;
        if (this.inWater)
        {
            this.speed *= 0.8;
        }
        if (this.isCollidedHorizontally)
        {
            this.speed *= 0.91;
        }
        else
        {
            this.speed *= 0.98;
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
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setHealth(this.getHealth() * 11.0F);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        //TODO take fire damage as heat then slowly damage the vehicle once it over heats
        //TODO reflect some arrow, and bullet damage
        //TODO reflect fall damage when we have only fell 6 blocks or bellow
        //TODO reflect most meele damage that is not from weapons
        //TODO take extra damage from explosion including damaging vehicle parts. As well knock player out of vehicle if it was right next to the vehicle
        //TODO on damage over X amount lose cargo
        //TODO ignore most spell damage as this is made of metal
        //TODO ignore all potions except those that are acids
        //TODO on radiation damage have the vehicle carry the radiation to damage players who use the vehicle
        if (this.isEntityInvulnerable() || source == DamageSource.cactus)
        {
            return false;
        }
        else if (!this.worldObj.isRemote && !this.isDead)
        {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setHealth(this.getHealth() + damage * 10.0F);
            this.setBeenAttacked();
            boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode;

            if (flag || this.getHealth() > this.maxDamage)
            {
                if (this.riddenByEntity != null)
                {
                    this.riddenByEntity.mountEntity(this);
                }

                if (!flag)
                {
                    //this.dropItemWithOffset(CoreRecipeLoader.itemVehicleTest.itemID, 1, 0.0F);
                    this.dropAsItem();
                }

                this.setDead();
            }

            return true;
        }
        else
        {
            return true;
        }
    }

    /** Called whe the vehicle is destory and should be dropped */
    public abstract void dropAsItem();

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
        return -.2;
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
