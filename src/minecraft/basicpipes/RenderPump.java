package basicpipes;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import basicpipes.pipes.TileEntityPipe;
import basicpipes.pipes.TileEntityPump;


public class RenderPump extends TileEntitySpecialRenderer
{
	int type = 0;
	private ModelPump model;
	
	public RenderPump()
	{
		model = new ModelPump();
	}

	public void renderAModelAt(TileEntityPump tileEntity, double d, double d1, double d2, float f)
	{
       
		bindTextureByName(BasicPipesMain.textureFile+"/Pump.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		
		model.renderMain(0.0625F);
		model.renderC1(0.0625F);
		model.renderC2(0.0625F);
		model.renderC3(0.0625F);
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) {
		this.renderAModelAt((TileEntityPump)tileEntity, var2, var4, var6, var8);
	}

}