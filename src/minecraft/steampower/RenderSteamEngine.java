package steampower;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import steampower.turbine.TileEntitySteamPiston;

public class RenderSteamEngine extends TileEntitySpecialRenderer
{
	int type = 0;
	private ModelEngine model;
	
	public RenderSteamEngine()
	{
		model = new ModelEngine();
	}


	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double d, double d1, double d2, float d3) {
		bindTextureByName(SteamPowerMain.textureFile+"Engine.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		
		float p = ((TileEntitySteamPiston)tileEntity).position;
		boolean cc = ((TileEntitySteamPiston)tileEntity).isConnected;
		int meta = ((TileEntityMachine) tileEntity).getDirection();
		switch(meta)
		{
			case 1:GL11.glRotatef(0f, 0f, 1f, 0f);break;
			case 2:GL11.glRotatef(90f, 0f, 1f, 0f);break;
			case 3:GL11.glRotatef(180f, 0f, 1f, 0f);break;
			case 4:GL11.glRotatef(270f, 0f, 1f, 0f);break;
		}
		if(cc)
		{
			model.renderTop(0.0625F);
			model.renderMid(0.0625F,p);
		}
			model.renderBot(0.0625F);
			
		
		GL11.glPopMatrix();
	}

}