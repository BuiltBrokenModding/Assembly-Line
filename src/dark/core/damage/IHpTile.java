package dark.core.damage;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public interface IHpTile
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

	/** Current hp of the tile */
	public int hp();

	/** Sets the tiles hp
	 * 
	 * @param i - amount
	 * @param increase - increase instead of replace */
	public void setHp(int i, boolean increase);

	/** Max hp of the object */
	public int getMaxHealth();

	/** Can the potion be used on the Entity that is translating damage for the TileEntity */
	public boolean canApplyPotion(PotionEffect par1PotionEffect);
}
