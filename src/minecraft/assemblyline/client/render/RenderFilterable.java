package assemblyline.client.render;

import java.util.ArrayList;

import assemblyline.common.machine.TileEntityFilterable;
import assemblyline.common.machine.filter.ItemFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class RenderFilterable extends TileEntitySpecialRenderer
{

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var8)
	{
		if (tileEntity instanceof TileEntityFilterable)
		{
			TileEntityFilterable filterable = (TileEntityFilterable) tileEntity;
			EntityPlayer p = Minecraft.getMinecraft().thePlayer;
			double dist = p.getDistance(filterable.xCoord, filterable.yCoord, filterable.zCoord);
			if (dist < 5)
			{
				ItemStack filter = filterable.getFilter();
				if (filter != null)
				{
					ArrayList<ItemStack> filters = ItemFilter.getFilters(filter);
					for (int i = 0; i < filters.size(); i++)
					{
						RenderHelper.renderFloatingText(filters.get(i).getTooltip(p, Minecraft.getMinecraft().gameSettings.advancedItemTooltips).get(0).toString(), (float) x + 0.5f, ((float) y + (i * 0.25f)) - 1f, (float) z + 0.5f);
					}
				}
			}
		}
	}

}
