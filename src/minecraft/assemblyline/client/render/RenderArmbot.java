package assemblyline.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import assemblyline.client.model.ModelArmbot;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.armbot.TileEntityArmbot;

public class RenderArmbot extends TileEntitySpecialRenderer
{
	public static final ModelArmbot MODEL = new ModelArmbot();
	public static final String TEXTURE = "armbot.png";

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var8)
	{
		if (tileEntity instanceof TileEntityArmbot)
		{
			this.bindTextureByName(AssemblyLine.TEXTURE_PATH + TEXTURE);
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
			GL11.glScalef(1.0F, -1F, -1F);
			MODEL.render(0.0625f, ((TileEntityArmbot) tileEntity).rotationYaw, ((TileEntityArmbot) tileEntity).rotationPitch);
			GL11.glPopMatrix();
		}
	}

}