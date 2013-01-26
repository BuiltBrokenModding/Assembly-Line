package assemblyline.common.machine.command;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import assemblyline.api.IArmbotUseable;

public class CommandUse extends Command
{
	private int times;
	private int curTimes;

	@Override
	public void onTaskStart()
	{
		times = 0;
		curTimes = 0;

		if (this.getArgs().length > 0)
		{
			times = this.getIntArg(0);
		}

		if (times <= 0)
			times = 1;
	}

	@Override
	protected boolean doTask()
	{
		TileEntity handTile = this.tileEntity.getHandPosition().getTileEntity(this.world);
		Entity handEntity = null;
		if (this.tileEntity.grabbedEntities.size() > 0)
			handEntity = this.tileEntity.grabbedEntities.get(0);
		if (handTile != null)
		{
			if (handTile instanceof IArmbotUseable)
			{
				((IArmbotUseable) handTile).onUse(this.tileEntity, handEntity);
			}
		}
		
		curTimes++;

		if (curTimes >= times)
			return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return "USE " + Integer.toString(this.times);
	}
}
