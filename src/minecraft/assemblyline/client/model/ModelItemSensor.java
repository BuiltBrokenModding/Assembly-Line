package assemblyline.client.model;

import static assemblyline.client.model.ModelHelper.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.client.ForgeHooksClient;

import assemblyline.common.AssemblyLine;
import assemblyline.common.CommonProxy;
import assemblyline.common.machine.sensor.TileItemSensor;

public class ModelItemSensor extends ModelBase
{

	public void render(TileItemSensor entity, double x, double y, double z)
	{
		glPushMatrix();
		glTranslated(x, y, z);

		ForgeHooksClient.bindTexture(AssemblyLine.TEXTURE_PATH + "sensor.png", 0);

		setGlobalTextureResolution(128, 128);
		setTextureClip(false);
		ModelHelper.setTextureOffset(0, 64);
		setTextureSubResolution(64, 64);
		drawCuboid(0.45f, 12f/16f, 0.45f, 2f/16f, 4f/16f, 2f/16f); //stand
		ModelHelper.setTextureOffset(0, 0);
		setTextureSubResolution(128, 64);
		drawCuboid(0.25f, 0.25f, 0.25f, 8f/16f, 8f/16f, 8f/16f); //block
		ModelHelper.setTextureOffset(64, 64);
		setTextureSubResolution(64, 32);
		drawCuboid(0.375f, 0.25f - (1f/16f), 0.375f, 4f/16f, 1f/16f, 4f/16f); //lens

		glPopMatrix();
	}

	public void render()
	{
		glPushMatrix();
		glScalef(1.5f, 1.5f, 1.5f);
		
		ForgeHooksClient.bindTexture(AssemblyLine.TEXTURE_PATH + "sensor.png", 0);

		setGlobalTextureResolution(128, 128);
		setTextureClip(false);
		ModelHelper.setTextureOffset(0, 64);
		setTextureSubResolution(64, 64);
		drawCuboid(0.45f, 12f/16f, 0.45f, 2f/16f, 4f/16f, 2f/16f); //stand
		ModelHelper.setTextureOffset(0, 0);
		setTextureSubResolution(128, 64);
		drawCuboid(0.25f, 0.25f, 0.25f, 8f/16f, 8f/16f, 8f/16f); //block
		ModelHelper.setTextureOffset(64, 64);
		setTextureSubResolution(64, 32);
		drawCuboid(0.375f, 0.25f - (1f/16f), 0.375f, 4f/16f, 1f/16f, 4f/16f); //lens
		
		glPopMatrix();
	}

}
