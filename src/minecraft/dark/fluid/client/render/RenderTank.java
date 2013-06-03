package dark.fluid.client.render;

import hydraulic.api.ColorCode;
import hydraulic.helpers.LiquidRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import dark.fluid.client.model.ModelTankSide;
import dark.fluid.common.FluidMech;
import dark.fluid.common.machines.TileEntityTank;

public class RenderTank extends TileEntitySpecialRenderer
{
	private ModelTankSide model;

	public RenderTank()
	{
		model = new ModelTankSide();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var8)
	{
		if (tileEntity instanceof TileEntityTank)
		{
			TileEntityTank tileEntityTank = ((TileEntityTank) tileEntity);
			ILiquidTank tank = tileEntityTank.getTank();
			LiquidStack liquid = tank.getLiquid();

			ColorCode color = tileEntityTank.getColor();
			boolean lre = false;
			if (liquid != null && liquid.amount > 0)
			{

				int[] displayList = LiquidRenderer.getLiquidDisplayLists(liquid, tileEntity.worldObj, false);
				if (displayList != null)
				{
					GL11.glPushMatrix();
					GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
					GL11.glEnable(GL11.GL_CULL_FACE);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					bindTextureByName(LiquidRenderer.getLiquidSheet(liquid));

					GL11.glTranslatef((float) x + 0.025F, (float) y, (float) z + 0.025F);
					GL11.glScalef(0.87F, 0.999F, 0.87F);
					GL11.glCallList(displayList[(int) ((float) Math.min(liquid.amount, tank.getCapacity()) / (float) (tank.getCapacity()) * (LiquidRenderer.DISPLAY_STAGES - 1))]);

					GL11.glPopAttrib();
					GL11.glPopMatrix();
					lre = true;
				}
			}
			bindTextureByName(FluidMech.MODEL_TEXTURE_DIRECTORY + "TankSide.png");
			GL11.glPushMatrix();

			GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
			GL11.glScalef(1.0F, -1F, -1F);

			model.render(0.0625F, false, false, false, false);
			GL11.glRotatef(90f, 0f, 1f, 0f);
			model.render(0.0625F, false, false, false, false);
			GL11.glRotatef(180f, 0f, 1f, 0f);
			model.render(0.0625F, false, false, false, false);
			GL11.glRotatef(270f, 0f, 1f, 0f);
			model.render(0.0625F, false, false, false, false);

			GL11.glPopMatrix();
		}
	}
}