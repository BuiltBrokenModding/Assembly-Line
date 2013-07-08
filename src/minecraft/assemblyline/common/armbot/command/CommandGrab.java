package assemblyline.common.armbot.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;

/**
 * Used by arms to search for entities in a region
 * 
 * @author Calclavia
 */
public class CommandGrab extends Command
{

	public static final float radius = 0.5f;
	/**
	 * If the grab command is specific to one entity this tell whether or not to grab the child version of that entity.
	 */
	public boolean child = false;

	private TileEntityConveyorBelt belt;
	/**
	 * The item to be collected.
	 */
	private Class<? extends Entity> entityToInclude;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();
		this.entityToInclude = Entity.class;
		if (this.getArgs() != null && this.getArgs().length > 0 && this.getArgs()[0] != null)
		{

			if (this.getArg(0).equalsIgnoreCase("baby") || this.getArg(0).equalsIgnoreCase("child"))
			{
				child = true;
				if (this.getArgs().length > 1 && this.getArgs()[1] != null)
				{
					this.entityToInclude = GrabDictionary.get(this.getArg(1)).getEntityClass();
				}
			}
			else
			{
				this.entityToInclude = GrabDictionary.get(this.getArg(0)).getEntityClass();
				if (this.getArgs().length > 1 && this.getArgs()[1] != null && (this.getArg(1).equalsIgnoreCase("baby") || this.getArg(0).equalsIgnoreCase("child")))
				{
					child = true;
				}
			}

		}
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.tileEntity.getGrabbedEntities().size() > 0) { return false; }

		Vector3 serachPosition = this.tileEntity.getHandPosition();
		List<Entity> found = this.world.getEntitiesWithinAABB(this.entityToInclude, AxisAlignedBB.getBoundingBox(serachPosition.x - radius, serachPosition.y - radius, serachPosition.z - radius, serachPosition.x + radius, serachPosition.y + radius, serachPosition.z + radius));
		
		TileEntity ent = serachPosition.getTileEntity(world);
		Vector3 searchPostion2 = Vector3.add(serachPosition, new Vector3(0, -1, 0));
		TileEntity ent2 = searchPostion2.getTileEntity(world);
		if (ent instanceof TileEntityConveyorBelt)
		{
			this.belt = (TileEntityConveyorBelt) ent;

		}
		else if (ent2 instanceof TileEntityConveyorBelt)
		{
			this.belt = (TileEntityConveyorBelt) ent2;
		}
		if (found != null && found.size() > 0)
		{
			for (int i = 0; i < found.size(); i++)
			{
				if (found.get(i) != null && !(found.get(i) instanceof EntityArrow) && !(found.get(i) instanceof EntityPlayer) && found.get(i).ridingEntity == null && (!(found.get(i) instanceof EntityAgeable) || (found.get(i) instanceof EntityAgeable && child == ((EntityAgeable) found.get(i)).isChild())))
				{
					this.tileEntity.grabEntity(found.get(i));
					this.world.playSound(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, "random.pop", 0.2F, ((this.tileEntity.worldObj.rand.nextFloat() - this.tileEntity.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F, true);
					if(this.belt != null){belt.ignoreEntity(found.get(i));}
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.child = taskCompound.getBoolean("child");
		this.entityToInclude = GrabDictionary.get(taskCompound.getString("name")).getEntityClass();
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		taskCompound.setBoolean("child", child);
		taskCompound.setString("name", ((this.entityToInclude != null) ? GrabDictionary.get(this.entityToInclude).getName() : ""));
	}

	@Override
	public String toString()
	{
		String baby = "";
		String entity = "";
		if (this.entityToInclude != null)
		{
			entity = GrabDictionary.get(this.entityToInclude).getName();
			if (this.child)
			{
				// TODO do check for EntityAgable
				baby = "baby ";
			}
		}
		return "GRAB " + baby + entity;
	}
}
