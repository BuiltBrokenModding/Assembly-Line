package dark.assembly.common.armbot.command;

import java.util.Random;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class CommandFire extends Command
{

    private static final float MIN_ACTUAL_PITCH = -80;
    private static final float MAX_ACTUAL_PITCH = 80;

    private float actualYaw;
    private float actualPitch;
    private float velocity;
    private Vector3 finalVelocity;

    public CommandFire()
    {
        super("throw");
    }

    @Override
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot, Object[] arguments)
    {
        super.onMethodCalled(world, location, armbot, arguments);

        this.velocity = UnitHelper.tryToParseFloat("" + this.getArg(0));
        if (this.velocity > 2.5f)
            this.velocity = 2.5f;
        if (this.velocity < 0.125f)
            this.velocity = 1f;

        this.actualYaw = (float) this.armbot.getRotation().x;
        this.actualPitch = ((MAX_ACTUAL_PITCH - MIN_ACTUAL_PITCH) * ((float) this.armbot.getRotation().y / 60f)) + MIN_ACTUAL_PITCH;

        double x, y, z;
        double yaw, pitch;
        yaw = Math.toRadians(actualYaw);
        pitch = Math.toRadians(actualPitch);
        // yaw = actualYaw;
        // pitch = actualPitch;

        x = -Math.sin(yaw) * Math.cos(pitch);
        y = Math.sin(pitch);
        z = Math.cos(yaw) * Math.cos(pitch);

        this.finalVelocity = new Vector3(x, y, z);
        Random random = new Random(System.currentTimeMillis());
        this.finalVelocity.x *= (1f - (1f / 200f)) + (random.nextFloat() * (1f / 100f));
        this.finalVelocity.y *= (1f - (1f / 200f)) + (random.nextFloat() * (1f / 100f));
        this.finalVelocity.z *= (1f - (1f / 200f)) + (random.nextFloat() * (1f / 100f));

        this.finalVelocity.scale(velocity);
        return true;
    }

    @Override
    public boolean onUpdate()
    {
        if (this.finalVelocity == null) // something went wrong
        {
            this.finalVelocity = new Vector3(0, 0, 0);
        }
        if (this.armbot.getGrabbedObjects().size() > 0)
        {
            Entity held = null;
            for (Object obj : this.armbot.getGrabbedObjects())
            {
                if (obj instanceof Entity)
                {
                    held = (Entity) obj;
                    break;
                }
            }
            if (held != null)
            {
                this.worldObj.playSound(this.armbotPos.x, this.armbotPos.y, this.armbotPos.z, "random.bow", velocity, 2f - (velocity / 4f), true);
                if (held instanceof EntityItem)
                {
                    EntityItem item = (EntityItem) held;
                    ItemStack stack = item.getEntityItem();
                    ItemStack thrown = stack.copy();
                    thrown.stackSize = 1;
                    if (item.getEntityItem().stackSize > 0)
                    {
                        stack.stackSize--;
                        item.setEntityItemStack(stack);
                    }
                    else
                    {
                        this.armbot.drop("all");
                        if (!this.worldObj.isRemote)
                        {
                            this.worldObj.removeEntity(held);
                        }
                    }
                    if (item.getEntityItem().itemID == Item.arrow.itemID)
                    {
                        EntityArrow arrow = new EntityArrow(worldObj, this.armbot.getHandPos().x, this.armbot.getHandPos().y, this.armbot.getHandPos().z);
                        arrow.motionX = this.finalVelocity.x;
                        arrow.motionY = this.finalVelocity.y;
                        arrow.motionZ = this.finalVelocity.z;
                        if (!this.worldObj.isRemote)
                            this.worldObj.spawnEntityInWorld(arrow);
                    }
                    else
                    {
                        EntityItem item2 = new EntityItem(worldObj, this.armbot.getHandPos().x, this.armbot.getHandPos().y, this.armbot.getHandPos().z, thrown);
                        item2.motionX = this.finalVelocity.x;
                        item2.motionY = this.finalVelocity.y;
                        item2.motionZ = this.finalVelocity.z;
                        if (!this.worldObj.isRemote)
                            this.worldObj.spawnEntityInWorld(item2);
                    }
                }
                else
                {
                    this.armbot.drop("all");
                    held.motionX = this.finalVelocity.x;
                    held.motionY = this.finalVelocity.y;
                    held.motionZ = this.finalVelocity.z;
                }
            }
        }

        return false;
    }

    @Override
    public Command readFromNBT(NBTTagCompound taskCompound)
    {
        super.readFromNBT(taskCompound);
        this.actualYaw = taskCompound.getFloat("fireYaw");
        this.actualPitch = taskCompound.getFloat("firePitch");
        this.velocity = taskCompound.getFloat("fireVelocity");
        this.finalVelocity = new Vector3();
        this.finalVelocity.x = taskCompound.getDouble("fireVectorX");
        this.finalVelocity.y = taskCompound.getDouble("fireVectorY");
        this.finalVelocity.z = taskCompound.getDouble("fireVectorZ");
        return this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound taskCompound)
    {
        super.writeToNBT(taskCompound);
        taskCompound.setFloat("fireYaw", this.actualYaw);
        taskCompound.setFloat("firePitch", this.actualPitch);
        taskCompound.setFloat("fireVelocity", this.velocity);
        if (this.finalVelocity != null)
        {
            taskCompound.setDouble("fireVectorX", this.finalVelocity.x);
            taskCompound.setDouble("fireVectorY", this.finalVelocity.y);
            taskCompound.setDouble("fireVectorZ", this.finalVelocity.z);
        }
        return taskCompound;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + Float.toString(this.velocity);
    }

    @Override
    public Command clone()
    {
        return new CommandFire();
    }
}
