package liquidmechanics.client.render;

import liquidmechanics.api.helpers.LiquidHelper;
import liquidmechanics.api.helpers.MHelper;
import liquidmechanics.client.model.ModelLiquidTank;
import liquidmechanics.client.model.ModelLiquidTankCorner;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.tileentity.TileEntityTank;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidContainerRegistry;

import org.lwjgl.opengl.GL11;


public class RenderTank extends TileEntitySpecialRenderer
{
	private LiquidHelper type = LiquidHelper.DEFUALT;
	private ModelLiquidTank model;
	private ModelLiquidTankCorner modelC;
	private int pos = 0;

	public RenderTank()
	{
		model = new ModelLiquidTank();
		modelC = new ModelLiquidTankCorner();
	}

	public void renderAModelAt(TileEntityTank te, double d, double d1, double d2, float f)
	{
		type = te.getType();
		if (te.tank.getLiquid() != null)
			pos = Math.min((te.tank.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME), 4);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		if (MHelper.corner(te) > 0)
		{
			bindTextureByName(LiquidMechanics.RESOURCE_PATH + "tanks/LiquidTankCorner.png");
			int corner = MHelper.corner(te);
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
			switch (type.ordinal())
			{
			// case 0:
			// bindTextureByName(BasicPipesMain.textureFile+"/pipes/SixSteamPipe.png");break;
				default:
					bindTextureByName(LiquidMechanics.RESOURCE_PATH + "tanks/LiquidTank" + pos + ".png");
					break;
			}
			model.renderMain(te, 0.0625F);
		}
		GL11.glPopMatrix();

	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
	{
		this.renderAModelAt((TileEntityTank) tileEntity, var2, var4, var6, var8);
	}

}