package dark.machines.common;

import java.util.EnumSet;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import dark.machines.common.blocks.BlockGasOre;

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

                    if (entity.isEntityAlive() && entity.isInsideOfMaterial(BlockGasOre.gas))
                    {
                        //TODO check for air supply or other parms to ignore damage
                        if (!flag)
                        {
                            entity.setAir(this.decreaseAirSupply(entity, entity.getAir()));

                            if (entity.getAir() == -20)
                            {
                                entity.setAir(0);
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
