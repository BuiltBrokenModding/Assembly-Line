package basicpipes;

import net.minecraft.src.ModelBase;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import basicpipes.pipes.TileEntityPipe;
import basicpipes.pipes.api.Liquid;


public class RenderPipe extends TileEntitySpecialRenderer
{
	Liquid type;
	int size = 6;
	
	private ModelPipe fourPipe;
	private ModelLargePipe SixPipe;
	private ModelBase model = fourPipe;
	
	public RenderPipe()
	{
		fourPipe = new ModelPipe();
		SixPipe = new ModelLargePipe();
	}

	public void renderAModelAt(TileEntityPipe tileEntity, double d, double d1, double d2, float f)
	{
        //Texture file
		
		type = tileEntity.getType();
		size = tileEntity.getSize();
		
        
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		if(size == 4)
		{
			switch(type.ordinal())
			{
				case 0: bindTextureByName(BasicPipesMain.textureFile+"/pipes/SteamPipe.png");break;
				case 1: bindTextureByName(BasicPipesMain.textureFile+"/pipes/WaterPipe.png");break;
				default:bindTextureByName(BasicPipesMain.textureFile+"/pipes/DefaultPipe.png"); break;
			}
			if(tileEntity.connectedBlocks[0] != null) fourPipe.renderBottom();
			if(tileEntity.connectedBlocks[1] != null) fourPipe.renderTop();
			if(tileEntity.connectedBlocks[2] != null) fourPipe.renderFront();
			if(tileEntity.connectedBlocks[3] != null) fourPipe.renderBack();
			if(tileEntity.connectedBlocks[4] != null) fourPipe.renderRight();
			if(tileEntity.connectedBlocks[5] != null) fourPipe.renderLeft();		
			fourPipe.renderMiddle();
		}
		if(size == 6)
		{
			switch(type.ordinal())
			{
				case 0: bindTextureByName(BasicPipesMain.textureFile+"/pipes/SixSteamPipe.png");break;
				default:bindTextureByName(BasicPipesMain.textureFile+"/pipes/DefaultPipe.png"); break;
			}
			if(tileEntity.connectedBlocks[0] != null) SixPipe.renderBottom();
			if(tileEntity.connectedBlocks[1] != null) SixPipe.renderTop();
			if(tileEntity.connectedBlocks[2] != null) SixPipe.renderFront();
			if(tileEntity.connectedBlocks[3] != null) SixPipe.renderBack();
			if(tileEntity.connectedBlocks[4] != null) SixPipe.renderRight();
			if(tileEntity.connectedBlocks[5] != null) SixPipe.renderLeft();		
			SixPipe.renderMiddle();
		}
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) {
		this.renderAModelAt((TileEntityPipe)tileEntity, var2, var4, var6, var8);
	}

}