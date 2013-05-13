package dark.library.npc.prefab;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.INpc;
import net.minecraft.world.World;

public class EntityNpc extends EntityLiving implements INpc
{

	public EntityNpc(World par1World)
	{
		super(par1World);
	}

	@Override
	public int getMaxHealth()
	{
		return 20;
	}

}
