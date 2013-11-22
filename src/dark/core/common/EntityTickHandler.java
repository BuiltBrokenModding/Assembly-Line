package dark.core.common;

import java.util.EnumSet;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

/** Tick handler that takes care of things like decreasing air supply while in gas block
 * 
 * @author DarkGuardsman */
public class EntityTickHandler implements ITickHandler
{

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        for (WorldServer world : DimensionManager.getWorlds())
        {
            for (Object o : world.loadedEntityList)
            {
                if (o instanceof EntityLivingBase)
                {
                    EntityLivingBase entity = (EntityLivingBase) o;

                    boolean flag = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.disableDamage;

                    if (entity.isEntityAlive() && entity.isInsideOfMaterial(Material.water))
                    {
                        if (!entity.canBreatheUnderwater() && !entity.isPotionActive(Potion.waterBreathing.id) && !flag)
                        {
                            entity.setAir(this.decreaseAirSupply(entity, entity.getAir()));

                            if (entity.getAir() == -20)
                            {
                                entity.setAir(0);

                                for (int i = 0; i < 8; ++i)
                                {
                                    float f = entity.worldObj.rand.nextFloat() - entity.worldObj.rand.nextFloat();
                                    float f1 = entity.worldObj.rand.nextFloat() - entity.worldObj.rand.nextFloat();
                                    float f2 = entity.worldObj.rand.nextFloat() - entity.worldObj.rand.nextFloat();
                                    entity.worldObj.spawnParticle("bubble", entity.posX + (double) f, entity.posY + (double) f1, entity.posZ + (double) f2, entity.motionX, entity.motionY, entity.motionZ);
                                }

                                entity.attackEntityFrom(DamageSource.drown, 2.0F);
                            }
                        }
                    }
                }
            }
        }

    }

    protected int decreaseAirSupply(EntityLivingBase entity, int par1)
    {
        int j = EnchantmentHelper.getRespiration(entity);
        return j > 0 && entity.worldObj.rand.nextInt(j + 1) > 0 ? par1 : par1 - 1;
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel()
    {
        return "[CoreMachine]EntityTickHandler";
    }

}
