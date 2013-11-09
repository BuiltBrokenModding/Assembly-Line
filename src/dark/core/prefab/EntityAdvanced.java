package dark.core.prefab;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/** Extended version of the entity
 * 
 * @author DarkGuardsman */
public abstract class EntityAdvanced extends Entity
{
    protected float maxDamage = 20;
    protected long ticks = 1;

    public EntityAdvanced(World world)
    {
        super(world);
        this.dataWatcher.addObject(6, Float.valueOf(1.0F));
        this.dataWatcher.addObject(7, Integer.valueOf(0));
        this.setHealth(this.getMaxHealth());
    }
    
    @Override
    protected void entityInit()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (this.ticks++ >= Long.MAX_VALUE - 10)
        {
            this.ticks = 1;
        }
        if (this.worldObj.isRemote)
        {
            this.updateAnimation();
        }
        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
    }

    /** Don't do any client side calls but rather update variables that control renders. For example
     * rotation of the model */
    public void updateAnimation()
    {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        this.setHealth(nbt.getFloat("Health"));

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("Health", this.getHealth());

    }

    public final float getHealth()
    {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float hp)
    {
        this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(hp, 0.0F, this.getMaxHealth())));
    }

    /** Max damage this entity can take */
    public float getMaxHealth()
    {
        return this.maxDamage;
    }

    /** Sets the time to count down from since the last time entity was hit. */
    public void setTimeSinceHit(int par1)
    {
        this.dataWatcher.updateObject(7, Integer.valueOf(par1));
    }

    /** Gets the time since the last hit. */
    public int getTimeSinceHit()
    {
        return this.dataWatcher.getWatchableObjectInt(7);
    }

}
