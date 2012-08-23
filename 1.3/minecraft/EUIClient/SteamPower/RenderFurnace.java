package EUIClient.SteamPower;

import org.lwjgl.opengl.GL11;

import EUI.SteamPower.SteamPower;
import net.minecraft.src.*;

public class RenderFurnace extends TileEntitySpecialRenderer
{
	int type = 0;
	private ModelFurnace model;
	
	public RenderFurnace()
	{
		model = new ModelFurnace();
	}


	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double d, double d1, double d2, float d3) {
		bindTextureByName(SteamPower.textureFile+"Furnace.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		model.genRender(0.0625F);
		GL11.glPopMatrix();
	}

}