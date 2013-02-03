package assemblyline.common.machine.command;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector3;

public class CommandFire extends Command
{
	private static final float	MIN_ACTUAL_PITCH	= -80;
	private static final float	MAX_ACTUAL_PITCH	= 80;

	private float				actualYaw;
	private float				actualPitch;
	private float				velocity;
	private Vector3				finalVelocity;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();

		this.velocity = this.getFloatArg(0);
		if (this.velocity > 2.5f)
			this.velocity = 2.5f;
		if (this.velocity < 0.125f)
			this.velocity = 1f;

		this.actualYaw = this.tileEntity.rotationYaw;
		this.actualPitch = ((MAX_ACTUAL_PITCH - MIN_ACTUAL_PITCH) * (this.tileEntity.rotationPitch / 60f)) + MIN_ACTUAL_PITCH;

		double x, y, z;
		double yaw, pitch;
		yaw = Math.toRadians(actualYaw);
		pitch = Math.toRadians(actualPitch);
		// yaw = actualYaw;
		// pitch = actualPitch;

		x = -Math.sin(yaw) * Math.cos(pitch);
		y = Math.sin(pitch);
		z = Math.cos(yaw) * Math.cos(pitch);

		this.finalVelocity = new Vector3(x, y, z);
		Random random = new Random(System.currentTimeMillis());
		this.finalVelocity.x *= (1f - (1f / 200f)) + (random.nextFloat() * (1f / 100f));
		this.finalVelocity.y *= (1f - (1f / 200f)) + (random.nextFloat() * (1f / 100f));
		this.finalVelocity.z *= (1f - (1f / 200f)) + (random.nextFloat() * (1f / 100f));

		this.finalVelocity.multiply(velocity);
	}

	@Override
	protected boolean doTask()
	{
		if (this.finalVelocity == null) // something went wrong
		{
			this.finalVelocity = new Vector3(0, 0, 0);
		}
		if (this.tileEntity.getGrabbedEntities().size() > 0)
		{
			Entity held = this.tileEntity.getGrabbedEntities().get(0);
			if (held != null)
			{
				this.world.playSound(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, "random.bow", velocity, 2f - (velocity / 4f), true);
				if (held instanceof EntityItem)
				{
					EntityItem item = (EntityItem) held;
					ItemStack stack = item.getEntityItem();
					ItemStack thrown = stack.copy();
					thrown.stackSize = 1;
					if (item.getEntityItem().stackSize > 0)
					{
						stack.stackSize--;
						item.func_92058_a(stack);
					}
					else
					{
						this.commandManager.getNewCommand(this.tileEntity, CommandDrop.class, new String[] {}).doTask();
						if (!this.world.isRemote)
							this.world.removeEntity(held);
					}
					if (item.getEntityItem().itemID == Item.arrow.itemID)
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
						EntityItem item2 = new EntityItem(world, this.tileEntity.getHandPosition().x, this.tileEntity.getHandPosition().y, this.tileEntity.getHandPosition().z, thrown);
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

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.actualYaw = taskCompound.getFloat("fireYaw");
		this.actualPitch = taskCompound.getFloat("firePitch");
		this.velocity = taskCompound.getFloat("fireVelocity");
		this.finalVelocity = new Vector3();
		this.finalVelocity.x = taskCompound.getDouble("fireVectorX");
		this.finalVelocity.y = taskCompound.getDouble("fireVectorY");
		this.finalVelocity.z = taskCompound.getDouble("fireVectorZ");
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		taskCompound.setFloat("fireYaw", this.actualYaw);
		taskCompound.setFloat("firePitch", this.actualPitch);
		taskCompound.setFloat("fireVelocity", this.velocity);
		if (this.finalVelocity != null)
		{
			taskCompound.setDouble("fireVectorX", this.finalVelocity.x);
			taskCompound.setDouble("fireVectorY", this.finalVelocity.y);
			taskCompound.setDouble("fireVectorZ", this.finalVelocity.z);
		}
	}

	@Override
	public String toString()
	{
		return "FIRE " + Float.toString(this.velocity);
	}
}
