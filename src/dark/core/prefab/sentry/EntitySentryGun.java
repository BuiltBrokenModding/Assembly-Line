package dark.core.prefab.sentry;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Since we are going to need an entity for the sentries to take damage with we are actually going
 * to do more with the entity than redirect damage.
 * 
 * @author DarkGuardsman */
public class EntitySentryGun extends Entity
{

    public EntitySentryGun(World par1World)
    {
        super(par1World);
    }

    @Override
    protected void entityInit()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        // TODO Auto-generated method stub
        
    }

}
