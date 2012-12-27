package assemblyline.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import assemblyline.client.model.ModelItemSensor;
import assemblyline.common.machine.sensor.TileItemSensor;

public class RenderItemSensor extends TileEntitySpecialRenderer
{
	ModelItemSensor model;
	
	public RenderItemSensor()
	{
		model = new ModelItemSensor();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var8)
	{
		if (tileEntity instanceof TileItemSensor)
		{
			model.render((TileItemSensor) tileEntity, x, y, z);
		}
	}

}
