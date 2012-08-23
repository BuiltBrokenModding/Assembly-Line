package EUIClient.SteamPower;

import org.lwjgl.opengl.GL11;

import EUI.SteamPower.SteamPower;

import net.minecraft.src.*;

public class RenderBoiler extends TileEntitySpecialRenderer
{
	int type = 0;
	private ModelTank model;
	
	public RenderBoiler(float par1)
	{
		model = new ModelTank(par1);
	}


	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double d, double d1, double d2, float d3) {
		bindTextureByName(SteamPower.textureFile+"tankTexture.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		model.generalRender(0.0625F);
		GL11.glPopMatrix();
	}

}