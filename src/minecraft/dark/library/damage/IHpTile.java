package dark.library.damage;

import net.minecraft.util.DamageSource;

public interface IHpTile
{
	/**
	 * Same as attackEntityFrom in Entity.class
	 * 
	 * @param source - DamageSource/DamageType
	 * @param ammount - amount of damage
	 * @return
	 */
	public boolean onDamageTaken(DamageSource source, int ammount);

	/**
	 * Is this tile considered too still be alive. Allows for the tile to remain while being
	 * considered dead
	 */
	public boolean isAlive();
}
