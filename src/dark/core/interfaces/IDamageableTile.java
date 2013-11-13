package dark.core.interfaces;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

/** Used by tiles that want to pretend to be living objects. Will require the use of this interface
 * as well spawning a EntityTileDamage entity as its location. Then entity if larger than the tile
 * will pass all interaction events to the block
 * 
 * @author DarkGuardsman */
public interface IDamageableTile extends IBlockActivated
{
    /** Same as attackEntityFrom in Entity.class
     * 
     * @param source - DamageSource/DamageType
     * @param ammount - amount of damage
     * @return */
    public boolean onDamageTaken(DamageSource source, float ammount);

    /** Is this tile considered too still be alive. Allows for the tile to remain while being
     * considered dead */
    public boolean isAlive();

    /** Current health of the tile */
    public float getDamage();

    /** Sets the tiles heath
     * 
     * @param health - amount hit points
     * @param increase - increase instead of replace */
    public void setDamage(float health);

    /** Max hit points of the object */
    public float getMaxHealth();

    /** Can the potion be used on the Entity that is translating damage for the TileEntity */
    public boolean canApplyPotion(PotionEffect par1PotionEffect);
}
