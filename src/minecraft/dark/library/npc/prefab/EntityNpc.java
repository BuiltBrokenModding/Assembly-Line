package dark.library.npc.prefab;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import dark.library.npc.IAdvancedNpc;
import dark.library.team.FactionInstance;

public class EntityNpc extends EntityCreature implements IAdvancedNpc
{
	private FactionInstance faction;
	private String humanName = "";

	public EntityNpc(World par1World)
	{
		super(par1World);
	}

	@Override
	public int getMaxHealth()
	{
		return 20;
	}

	@Override
	public FactionInstance getFaction()
	{
		return this.faction;
	}

	@Override
	public String getName()
	{
		return humanName;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		nbt.setString("humanName", this.humanName);
		nbt.setTag("faction", faction.write());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		
		
	}

}
