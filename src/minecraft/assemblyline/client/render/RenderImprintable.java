package assemblyline.client.render;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.core.vector.Vector3;
import assemblyline.common.machine.imprinter.ItemImprinter;
import assemblyline.common.machine.imprinter.TileEntityImprintable;

/**
 * @author Briman0094
 */
public abstract class RenderImprintable extends TileEntitySpecialRenderer
{
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var8)
	{
		if (tileEntity != null)
		{
			if (tileEntity instanceof TileEntityImprintable)
			{
				TileEntityImprintable tileFilterable = (TileEntityImprintable) tileEntity;

				ItemStack filter = tileFilterable.getFilter();

				if (filter != null)
				{
					EntityPlayer player = Minecraft.getMinecraft().thePlayer;
					MovingObjectPosition objectPosition = player.rayTrace(5, 1);

					if (objectPosition != null)
					{
						if (objectPosition.blockX == tileFilterable.xCoord && objectPosition.blockY == tileFilterable.yCoord && objectPosition.blockZ == tileFilterable.zCoord)
						{
							ArrayList<ItemStack> filters = ItemImprinter.getFilters(filter);
							for (int i = 0; i < filters.size(); i++)
							{
								if (((TileEntityImprintable) tileEntity).isInverted())
								{
									RenderHelper.renderFloatingText(filters.get(i).getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips).get(0).toString(), (float) x + 0.5f, ((float) y + (i * 0.25f)) - 1f, (float) z + 0.5f, 1f, 0.5f, 0.5f);
								}
								else
								{
									RenderHelper.renderFloatingText(filters.get(i).getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips).get(0).toString(), (float) x + 0.5f, ((float) y + (i * 0.25f)) - 1f, (float) z + 0.5f, 0.5f, 1f, 0.5f);
								}
							}
						}
					}
				}
			}
		}
	}

}
