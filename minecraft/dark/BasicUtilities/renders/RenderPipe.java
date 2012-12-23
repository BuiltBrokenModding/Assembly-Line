package dark.BasicUtilities.renders;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.Tile.TileEntityPipe;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.renders.models.ModelLargePipe;
import dark.BasicUtilities.renders.models.ModelPipe;


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
		
        
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
			switch(type.ordinal())
			{
				case 0: bindTextureByName(BasicUtilitiesMain.textureFile+"pipes/SixSteamPipe.png");break;
				case 1: bindTextureByName(BasicUtilitiesMain.textureFile+"pipes/SixWaterPipe.png");break;
				case 2: bindTextureByName(BasicUtilitiesMain.textureFile+"pipes/SixLavaPipe.png");break;
				case 3: bindTextureByName(BasicUtilitiesMain.textureFile+"pipes/SixOilPipe.png");break;
				default:bindTextureByName(BasicUtilitiesMain.textureFile+"pipes/DefaultPipe.png"); break;
			}
			if(tileEntity.connectedBlocks[0] != null) SixPipe.renderBottom();
			if(tileEntity.connectedBlocks[1] != null) SixPipe.renderTop();
			if(tileEntity.connectedBlocks[3] != null) SixPipe.renderFront();
			if(tileEntity.connectedBlocks[2] != null) SixPipe.renderBack();
			if(tileEntity.connectedBlocks[5] != null) SixPipe.renderRight();
			if(tileEntity.connectedBlocks[4] != null) SixPipe.renderLeft();		
			SixPipe.renderMiddle();
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) {
		this.renderAModelAt((TileEntityPipe)tileEntity, var2, var4, var6, var8);
	}

}