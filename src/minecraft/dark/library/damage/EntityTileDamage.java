package dark.library.damage;

import universalelectricity.core.vector.Vector3;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * Entity designed to take damage and apply it to the tile from an Entity. Simulates the tile is
 * alive and can be harmed by normal AIs without additional code.
 * 
 * @author DarkGuardsman
 * 
 */
public class EntityTileDamage extends Entity implements IEntityAdditionalSpawnData
{

	private TileEntity host;
	int hp = 100;

	public EntityTileDamage(World par1World)
	{
		super(par1World);
		this.setSize(1F, 1F);
	}

	public EntityTileDamage(World par1World, TileEntity c)
	{
		this(par1World);
		this.isImmuneToFire = true;
		this.setPosition(c.xCoord + 0.5, c.yCoord + 0.5, c.zCoord + 0.5);
		this.host = c;
	}

	@Override
	protected void entityInit()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, int ammount)
	{
		if (this.isEntityInvulnerable())
		{
			return false;
		}
		else if (this.host instanceof IHpTile)
		{
			return ((IHpTile) this.host).onDamageTaken(source, ammount);
		}
		else
		{
			this.hp -= ammount;
			if (hp <= 0)
			{
				if (this.host != null)
				{
					Vector3 vec = new Vector3(this.host.xCoord, this.host.yCoord, this.host.zCoord);
					int id = vec.getBlockID(this.worldObj);
					int meta = vec.getBlockID(this.worldObj);
					Block block = Block.blocksList[id];
					if (block != null)
					{
						block.breakBlock(this.worldObj, this.host.xCoord, this.host.yCoord, this.host.zCoord, id, meta);
					}
					vec.setBlock(this.worldObj, 0);
				}
				this.setDead();

			}
			return false;
		}
	}

	@Override
	public String getEntityName()
	{
		return "EntityTileTarget";
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		if (this.host != null)
		{
			data.writeInt(this.host.xCoord);
			data.writeInt(this.host.yCoord);
			data.writeInt(this.host.zCoord);
		}
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.host = this.worldObj.getBlockTileEntity(data.readInt(), data.readInt(), data.readInt());
	}

	@Override
	public void onUpdate()
	{
		if (this.host == null || this.host.isInvalid())
		{
			this.setDead();
			return;
		}
		if (this.host instanceof IHpTile && !((IHpTile) this.host).isAlive())
		{
			this.setDead();
			return;
		}
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

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

}
