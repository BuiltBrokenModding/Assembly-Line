package fluidmech.client.render;

import hydraulic.api.ColorCode;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import dark.library.helpers.ConnectionHelper;

import fluidmech.client.model.ModelLiquidTank;
import fluidmech.client.model.ModelLiquidTankCorner;
import fluidmech.common.FluidMech;
import fluidmech.common.tiles.TileEntityTank;

public class RenderTank extends TileEntitySpecialRenderer
{
	private ModelLiquidTank model;
	private ModelLiquidTankCorner modelC;

	public RenderTank()
	{
		model = new ModelLiquidTank();
		modelC = new ModelLiquidTankCorner();
	}

	public void renderAModelAt(TileEntityTank te, double d, double d1, double d2, float f)
	{
		int meta = te.getBlockMetadata();
		int guageMeta = meta;
		LiquidStack stack = te.getStoredLiquid();
		int pos = 0;
		if (stack != null)
		{
			pos = Math.min((stack.amount / LiquidContainerRegistry.BUCKET_VOLUME), 4);
			if (meta == ColorCode.NONE.ordinal())
			{
				guageMeta = ColorCode.get(stack).ordinal();
			}
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);

		if (ConnectionHelper.corner(te) > 0)
		{
			bindTextureByName(this.getCornerTexture(meta));
			int corner = ConnectionHelper.corner(te);
			switch (corner)
			{
				case 2:
					GL11.glRotatef(270f, 0f, 1f, 0f);
					break;
				case 3:
					GL11.glRotatef(0f, 0f, 1f, 0f);
					break;
				case 4:
					GL11.glRotatef(90f, 0f, 1f, 0f);
					break;
				case 1:
					GL11.glRotatef(180f, 0f, 1f, 0f);
					break;
			}
			modelC.render(0.0625F);
		}
		else
		{
			bindTextureByName(this.getTankTexture(meta));
			model.renderMain(0.0625F);
			bindTextureByName(this.getGuageTexture(guageMeta, pos));
			model.renderMeter(te, 0.0625F);
		}
		GL11.glPopMatrix();

	}

	public static String getTankTexture(int meta)
	{
		String type = "";
		switch (ColorCode.get(meta))
		{
			case RED:
				type = "Lava";
				break;
			case BLUE:
				type = "Water";
				break;
			case WHITE:
				type = "Milk";
				break;
			case ORANGE:
				type = "Steam";
				break;
			default:
				type = "";
				break;
		}

		return FluidMech.MODEL_TEXTURE_DIRECTORY + "tanks/" + type + "Tank.png";

	}

	public static String getGuageTexture(int meta, int pos)
	{
		String type = "";
		switch (ColorCode.get(meta))
		{
			case RED:
				type = "Lava";
				break;
			case YELLOW:
				type = "Fuel";
				break;
			default:
				type = "";
				break;
		}

		return FluidMech.MODEL_TEXTURE_DIRECTORY + "tanks/guage/" + pos + type + ".png";
	}

	public static String getCornerTexture(int meta)
	{
		String type = "";
		switch (ColorCode.get(meta))
		{
			case RED:
				type = "Lava";
				break;
			case BLUE:
				type = "Water";
				break;
			case WHITE:
				type = "Milk";
				break;
			default:
				type = "";
				break;
		}
		return FluidMech.MODEL_TEXTURE_DIRECTORY + "tanks/Corner" + type + ".png";

	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
	{
		this.renderAModelAt((TileEntityTank) tileEntity, var2, var4, var6, var8);
	}

}