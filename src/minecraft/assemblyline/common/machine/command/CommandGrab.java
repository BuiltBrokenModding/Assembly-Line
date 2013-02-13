package assemblyline.common.machine.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
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
	 * If the grab command is specific to one entity this tell whether or not to grab the child version of that entity.
	 */
	public boolean child = false;
	/**
	 * The item to be collected.
	 */
	private Class<? extends Entity> entityToInclude;

	public CommandGrab()
	{
		super();
		this.entityToInclude = EntityItem.class;
		if (this.parameters != null && this.parameters.length > 0 && this.parameters[0] != null)
		{
			if (this.getArg(0).equalsIgnoreCase("baby") || this.getArg(0).equalsIgnoreCase("child"))
			{
				child = true;
				if (this.parameters.length > 1 && this.parameters[1] != null)
				{
					this.entityToInclude = GrabDictionary.get(this.getArg(0)).getEntityClass();
				}
			}
			else
			{
				this.entityToInclude = GrabDictionary.get(this.getArg(0)).getEntityClass();
				if (this.parameters.length > 1 && this.parameters[1] != null && (this.getArg(1).equalsIgnoreCase("baby") || this.getArg(0).equalsIgnoreCase("child")))
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

		if (found != null && found.size() > 0)
		{
			for (int i = 0; i < found.size(); i++)
			{
				if (found.get(i) != null && !(found.get(i) instanceof EntityArrow) && found.get(i).ridingEntity == null && (found.get(i) instanceof EntityAgeable && (child && ((EntityAgeable) found.get(i)).isChild() || (!child && !((EntityAgeable) found.get(i)).isChild()))))
				{
					this.tileEntity.grabEntity(found.get(i));
					this.world.playSound(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, "random.pop", 0.2F, ((this.tileEntity.worldObj.rand.nextFloat() - this.tileEntity.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F, true);
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public String toString()
	{
		return "GRAB";
	}
}
