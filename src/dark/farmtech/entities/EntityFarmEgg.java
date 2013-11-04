package dark.farmtech.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityFarmEgg extends EntityThrowable
{
    int id = 0;

    public EntityFarmEgg(World world, int id)
    {
        super(world);
        this.id = id;
    }

    public EntityFarmEgg(World world, EntityLivingBase entityLivingBase, int id)
    {
        super(world, entityLivingBase);
        this.id = id;
    }

    public EntityFarmEgg(World world, double x, double y, double z, int id)
    {
        super(world, x, y, z);
        this.id = id;
    }

    /** Called when this EntityThrowable hits a block or entity. */
    protected void onImpact(MovingObjectPosition mop)
    {
        if (mop.entityHit != null)
        {
            mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0)
        {
            EntityAnimal animal;
            switch(id)
            {
                default: animal = new EntityChicken(this.worldObj); break;
            }
            if (animal != null)
            {
                animal.setGrowingAge(-24000);
                animal.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                this.worldObj.spawnEntityInWorld(animal);
            }
        }

        for (int j = 0; j < 8; ++j)
        {
            this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!this.worldObj.isRemote)
        {
            this.setDead();
        }
    }
}
