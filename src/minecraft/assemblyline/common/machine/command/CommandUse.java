package assemblyline.common.machine.command;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import assemblyline.common.machine.armbot.IUseable;

public class CommandUse extends Command
{
	@Override
	protected boolean doTask()
	{
		TileEntity handTile = this.tileEntity.getHandPosition().getTileEntity(this.world);
		Entity handEntity = null;
		if (this.tileEntity.grabbedEntities.size() > 0)
			handEntity = this.tileEntity.grabbedEntities.get(0);
		if (handTile != null)
		{
			if (handTile instanceof IUseable)
			{
				((IUseable) handTile).onUse(this.tileEntity, handEntity);
			}
		}
		return false;
	}
}
