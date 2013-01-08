package assemblyline.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import assemblyline.client.model.ModelArmbot;

public class RenderArmbot extends TileEntitySpecialRenderer
{
	public static final ModelArmbot MOEDL = new ModelArmbot();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var8)
	{
		// this.bindTextureByName(AssemblyLine.TEXTURE_PATH + "sorter.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		MOEDL.render(0.0625f);
		GL11.glPopMatrix();
	}

}