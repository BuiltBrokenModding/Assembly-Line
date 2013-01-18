package assemblyline.common.machine.command;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

public class CommandDrop extends Command
{
	@Override
	protected boolean doTask()
	{
		super.doTask();

		// TODO: Animate Armbot to move down and drop all items.
		for (Entity entity : this.tileEntity.grabbedEntities)
		{
			if (entity != null)
			{
				entity.isDead = false;
				entity.worldObj = this.tileEntity.worldObj;
				if (entity instanceof EntityItem)
					world.spawnEntityInWorld(entity); //items don't move right, so we render them manually
				Minecraft.getMinecraft().sndManager.playSound("random.pop", this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, 0.2F, ((this.tileEntity.worldObj.rand.nextFloat() - this.tileEntity.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 1.0F);
			}
		}
		this.tileEntity.grabbedEntities.clear();
		return false;
	}
}
