package basicpipes.renderTank;

import net.minecraft.src.ModelBase;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import basicpipes.BasicPipesMain;
import basicpipes.ModelLargePipe;
import basicpipes.ModelPipe;
import basicpipes.LTanks.TileEntityLTank;
import basicpipes.conductors.TileEntityPipe;
import basicpipes.pipes.api.Liquid;


public class RenderLTank extends TileEntitySpecialRenderer
{
	private Liquid type = Liquid.DEFUALT;
	private ModelLiquidTank model;
	private int pos = 0;
	
	public RenderLTank()
	{
		model = new ModelLiquidTank();
	}

	public void renderAModelAt(TileEntityLTank te, double d, double d1, double d2, float f)
	{
		type = te.getType();
		pos = Math.min(te.getStoredLiquid(type),4);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		
			switch(type.ordinal())
			{
				//case 0: bindTextureByName(BasicPipesMain.textureFile+"/pipes/SixSteamPipe.png");break;
				default:bindTextureByName(BasicPipesMain.textureFile+"/tanks/LiquidTank"+pos+".png"); break;
			}
			model.renderMain(te, 0.0625F);
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) {
		this.renderAModelAt((TileEntityLTank)tileEntity, var2, var4, var6, var8);
	}

}