package basicpipes;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import basicpipes.pipes.TileEntityPipe;


public class RenderPipe extends TileEntitySpecialRenderer
{
	int type = 0;
	private ModelPipe model;
	private ModelPipe model2;
	
	public RenderPipe()
	{
		model = new ModelPipe();
		model2 = new ModelPipe();
	}

	public void renderAModelAt(TileEntityPipe tileEntity, double d, double d1, double d2, float f)
	{
        //Texture file
		
		type = tileEntity.getType();
		switch(type)
		{
		case 0: bindTextureByName(BasicPipesMain.textureFile+"/pipes/SteamPipe.png");break;
		case 1: bindTextureByName(BasicPipesMain.textureFile+"/pipes/WaterPipe.png");break;
		default:bindTextureByName(BasicPipesMain.textureFile+"/pipes/DefaultPipe.png"); break;
		}
        
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);

		if(tileEntity.connectedBlocks[0] != null) model.renderBottom();
		if(tileEntity.connectedBlocks[1] != null) model.renderTop();
		if(tileEntity.connectedBlocks[2] != null) model.renderFront();
		if(tileEntity.connectedBlocks[3] != null) model.renderBack();
		if(tileEntity.connectedBlocks[4] != null) model.renderRight();
		if(tileEntity.connectedBlocks[5] != null) model.renderLeft();
		
		model.renderMiddle();
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) {
		this.renderAModelAt((TileEntityPipe)tileEntity, var2, var4, var6, var8);
	}

}