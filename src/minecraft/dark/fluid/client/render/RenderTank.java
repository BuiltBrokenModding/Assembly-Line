package dark.fluid.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import dark.core.api.ColorCode;
import dark.fluid.client.model.ModelTankSide;
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
		FluidStack liquid = tileEntity instanceof TileEntityTank ? ((TileEntityTank) tileEntity).getTank().getFluid() : null;
		this.renderTank(tileEntity, x, y, z, 0, liquid);
	}

	public void renderTank(TileEntity tileEntity, double x, double y, double z, int meta, FluidStack liquid)
	{
		int[] render = new int[6];
		ColorCode color = ColorCode.get(meta >= 0 && meta < ColorCode.values().length ? meta : 0);
		if (tileEntity instanceof TileEntityTank)
		{
			render = ((TileEntityTank) tileEntity).renderConnection;
			color = ((TileEntityTank) tileEntity).getColor();
		}
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

				GL11.glTranslatef((float) x, (float) y, (float) z);
				GL11.glScalef(1.01F, 1.01F, 1.01F);
				int cap = (tileEntity instanceof TileEntityTank ? ((TileEntityTank) tileEntity).getTank().getCapacity() : 8000);
				GL11.glCallList(displayList[(int) ((float) Math.min(liquid.amount, cap) / (float) (cap) * (LiquidRenderer.DISPLAY_STAGES - 1))]);

				GL11.glPopAttrib();
				GL11.glPopMatrix();
			}
		}
		// DarkMain.TEXTURE_DIRECTORY + "CobbleBack.png"
		if (color == ColorCode.RED)
		{
			bindTextureByName("/textures/blocks/obsidian.png");
		}
		else
		{
			bindTextureByName("/textures/blocks/stonebrick.png");
		}

		boolean bot = render[1] == 2;
		boolean top = render[0] == 2;
		boolean north = render[2] == 2;
		boolean south = render[3] == 2;
		boolean east = render[5] == 2;
		boolean west = render[4] == 2;
		for (int i = 0; i < 4; i++)
		{
			ForgeDirection dir = ForgeDirection.getOrientation(i + 2);
			if (render[dir.getOpposite().ordinal()] != 2)
			{
				GL11.glPushMatrix();
				GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
				GL11.glScalef(1.0F, -1F, -1F);
				boolean left = false;
				boolean right = false;
				switch (dir)
				{
					case NORTH:
						GL11.glRotatef(180f, 0f, 1f, 0f);
						left = west;
						right = east;
						break;
					case SOUTH:
						GL11.glRotatef(0f, 0f, 1f, 0f);
						left = east;
						right = west;
						break;
					case WEST:
						GL11.glRotatef(90f, 0f, 1f, 0f);
						left = south;
						right = north;
						break;
					case EAST:
						GL11.glRotatef(270f, 0f, 1f, 0f);
						left = north;
						right = south;
						break;
				}

				model.render(0.0625F, left, right, top, bot);

				GL11.glPopMatrix();
			}
		}

	}
}