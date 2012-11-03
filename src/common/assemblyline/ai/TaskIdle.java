package assemblyline.ai;

import net.minecraft.src.TileEntity;

public class TaskIdle extends Task
{
	private TileEntity tileEntity;

	@Override
	public void setTileEntity(TileEntity tileEntity)
	{
		this.tileEntity = tileEntity;
	}
}
