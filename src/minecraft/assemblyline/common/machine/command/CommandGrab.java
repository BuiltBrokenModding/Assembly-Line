package assemblyline.common.machine.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

/**
 * Used by arms to search for entities in a region
 * 
 * @author Calclavia
 */
public class CommandGrab extends Command
{

	public static final float radius = 0.5f;

	/**
	 * The item to be collected.
	 */
	private Class<? extends Entity> entityToInclude;

	public CommandGrab()
	{
		super();
		this.entityToInclude = Entity.class;
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.tileEntity.grabbedEntities.size() > 0)
			return false;

		Vector3 serachPosition = this.tileEntity.getHandPosition();
		List<Entity> found = this.world.getEntitiesWithinAABB(this.entityToInclude, AxisAlignedBB.getBoundingBox(serachPosition.x - radius, serachPosition.y - radius, serachPosition.z - radius, serachPosition.x + radius, serachPosition.y + radius, serachPosition.z + radius));

		if (found != null && found.size() > 0)
		{
			for (int i = 0; i < found.size(); i++)
			{
				if (found.get(i) != null && !(found.get(i) instanceof EntityPlayer) && !(found.get(i) instanceof EntityArrow) && found.get(i).ridingEntity == null) // isn't
																																									// null,
																																									// isn't
																																									// a
																																									// player,
																																									// and
																																									// isn't
																																									// riding
																																									// anything
				{
					this.tileEntity.grabbedEntities.add(found.get(i));
					if (found.get(i) instanceof EntityItem)
						this.tileEntity.worldObj.removeEntity(found.get(i)); // items don't move
																				// right, so we
																				// render them
																				// manually\
					this.world.playSound(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, "random.pop", 0.2F, ((this.tileEntity.worldObj.rand.nextFloat() - this.tileEntity.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F, true);
					found.get(i).isDead = false;
					return false;
				}
			}
		}
		/**
		 * Move the robotic arm around and emulate an item search. Then initiate a collect task.
		 */

		return true;
	}
	
	@Override
	public String toString()
	{
		return "GRAB";
	}
}
