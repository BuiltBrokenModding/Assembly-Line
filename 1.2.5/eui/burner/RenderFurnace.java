package net.minecraft.src.eui.burner;

import org.lwjgl.opengl.GL11;
import net.minecraft.src.*;

public class RenderFurnace extends TileEntitySpecialRenderer
{
	int type = 0;
	private FurnaceModel model;
	
	public RenderFurnace()
	{
		model = new FurnaceModel();
	}


	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double d, double d1, double d2, float d3) {
		bindTextureByName("/eui/Furnace.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		model.genRender(0.0625F);
		GL11.glPopMatrix();
	}

}