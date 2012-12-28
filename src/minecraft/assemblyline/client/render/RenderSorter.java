package assemblyline.client.render;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import assemblyline.client.model.ModelSorter;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.TileEntityRejector;
import assemblyline.common.machine.filter.ItemFilter;

public class RenderSorter extends TileEntitySpecialRenderer
{
	private ModelSorter model = new ModelSorter();

	private void renderAModelAt(TileEntityRejector tileEntity, double x, double y, double z, float f)
	{
		boolean fire = tileEntity.firePiston;
		int face = tileEntity.getDirection().ordinal();
		int pos = 0;

		if (fire)
		{
			pos = 8;
		}
		bindTextureByName(AssemblyLine.TEXTURE_PATH + "sorter.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		if (face == 2)
		{
			GL11.glRotatef(180f, 0f, 1f, 0f);
		}
		if (face == 3)
		{
			GL11.glRotatef(0f, 0f, 1f, 0f);
		}
		else if (face == 4)
		{
			GL11.glRotatef(90f, 0f, 1f, 0f);
		}
		else if (face == 5)
		{
			GL11.glRotatef(270f, 0f, 1f, 0f);
		}
		model.renderMain(0.0625F);
		model.renderPiston(0.0625F, pos);
		GL11.glPopMatrix();

		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		double dist = p.getDistance(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		System.out.println(dist);
		if (dist < 5)
		{
			ItemStack filter = tileEntity.getFilter();
			if (filter != null)
			{
				ArrayList<ItemStack> filters = ItemFilter.getFilters(filter);
				for (int i = 0; i < filters.size(); i++)
				{
					RenderHelper.renderFloatingText(filters.get(i).getDisplayName(), (float) x + 0.5f, ((float) y + (i * 0.25f)) - 1f, (float) z + 0.5f);
				}
			}
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
	{
		this.renderAModelAt((TileEntityRejector) tileEntity, var2, var4, var6, var8);
	}

}