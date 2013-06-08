package dark.library.damage;

import universalelectricity.prefab.CustomDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class TileDamageSource extends CustomDamageSource
{
	protected Object damageSource;

	public static final CustomDamageSource bullets = ((CustomDamageSource) new CustomDamageSource("Bullets")).setDeathMessage("%1$s was filled with holes!");
	public static final CustomDamageSource laser = ((CustomDamageSource) new CustomDamageSource("Laser")).setDeathMessage("%1$s was vaporized!");

	public TileDamageSource(String damageName, Object attacker)
	{
		super(damageName);
		this.damageSource = attacker;
	}

	@Override
	public Entity getEntity()
	{
		return damageSource instanceof Entity ? (Entity) damageSource : null;
	}

	@Override
	public boolean isDifficultyScaled()
	{
		return this.damageSource != null && this.damageSource instanceof EntityLiving && !(this.damageSource instanceof EntityPlayer);
	}
}
