package assemblyline.common.machine.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

public class CommandDrop extends Command
{
	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.tileEntity.grabbedEntities.size() == 0)
			return false;

		for (Entity entity : this.tileEntity.grabbedEntities)
		{
			if (entity != null)
			{
				entity.isDead = false;
				entity.worldObj = this.tileEntity.worldObj;

				if (entity instanceof EntityItem)
				{
					if (!world.isRemote)
					{
						// TODO: This causes crash.
						// world.spawnEntityInWorld(entity);
					}
				}
			}
		}

		this.world.playSound(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, "random.pop", 0.2F, ((this.tileEntity.worldObj.rand.nextFloat() - this.tileEntity.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F, true);

		this.tileEntity.grabbedEntities.clear();
		return false;
	}

	@Override
	public String toString()
	{
		return "DROP";
	}
}
