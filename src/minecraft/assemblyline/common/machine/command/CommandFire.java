package assemblyline.common.machine.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import universalelectricity.core.vector.Vector3;

public class CommandFire extends Command
{
	private static final float	MIN_ACTUAL_PITCH	= 0;
	private static final float	MAX_ACTUAL_PITCH	= 100;
	private static final float	VELOCITY			= 2f;

	private float				actualYaw;
	private float				actualPitch;
	private Vector3				finalVelocity;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();
		this.actualYaw = this.tileEntity.rotationYaw;
		this.actualPitch = ((MAX_ACTUAL_PITCH - MIN_ACTUAL_PITCH) * (this.tileEntity.rotationPitch / 60f)) + MIN_ACTUAL_PITCH;

		double x, y, z;
		double yaw, pitch;
		yaw = Math.toRadians(actualYaw);
		pitch = Math.toRadians(actualPitch);
		// yaw = actualYaw;
		// pitch = actualPitch;

		x = Math.sin(yaw) * Math.cos(pitch);
		y = Math.sin(pitch);
		z = Math.cos(yaw) * Math.cos(pitch);

		this.finalVelocity = new Vector3(x, y, z);

		this.finalVelocity.multiply(VELOCITY);
	}

	@Override
	protected boolean doTask()
	{
		if (this.finalVelocity == null) // something went wrong
		{
			this.finalVelocity = new Vector3(0, 0, 0);
		}
		if (this.tileEntity.grabbedEntities.size() > 0)
		{
			Entity held = this.tileEntity.grabbedEntities.get(0);
			if (held != null)
			{
				if (held instanceof EntityItem)
				{
					EntityItem item = (EntityItem) held;
					if (item.func_92014_d().stackSize > 1)
					{
						item.func_92014_d().stackSize--;
					}
					else
					{
						this.commandManager.getNewCommand(this.tileEntity, CommandDrop.class, new String[] {}).doTask();
						if (!this.world.isRemote)
							this.world.removeEntity(held);
					}
					if (item.func_92014_d().itemID == Item.arrow.itemID)
					{
						EntityArrow arrow = new EntityArrow(world, this.tileEntity.getHandPosition().x, this.tileEntity.getHandPosition().y, this.tileEntity.getHandPosition().z);
						arrow.motionX = this.finalVelocity.x;
						arrow.motionY = this.finalVelocity.y;
						arrow.motionZ = this.finalVelocity.z;
						if (!this.world.isRemote)
							this.world.spawnEntityInWorld(arrow);
					}
					else
					{
						EntityItem item2 = new EntityItem(world, this.tileEntity.getHandPosition().x, this.tileEntity.getHandPosition().y, this.tileEntity.getHandPosition().z, item.func_92014_d());
						item2.motionX = this.finalVelocity.x;
						item2.motionY = this.finalVelocity.y;
						item2.motionZ = this.finalVelocity.z;
						if (!this.world.isRemote)
							this.world.spawnEntityInWorld(item2);
					}
				}
				else
				{
					this.commandManager.getNewCommand(this.tileEntity, CommandDrop.class, new String[] {}).doTask();
					held.motionX = this.finalVelocity.x;
					held.motionY = this.finalVelocity.y;
					held.motionZ = this.finalVelocity.z;
				}
			}
		}

		return false;
	}
}
