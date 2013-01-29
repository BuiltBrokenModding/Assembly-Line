package assemblyline.client.render;

import static org.lwjgl.opengl.GL11.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import assemblyline.client.model.ModelCraneController;
import assemblyline.common.AssemblyLine;

public class RenderCraneController extends RenderImprintable
{
	public static final String					TEXTURE	= "QuarryControllerMap.png";
	public static final ModelCraneController	MODEL	= new ModelCraneController();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
	{
		this.bindTextureByName(AssemblyLine.TEXTURE_PATH + TEXTURE);
		ForgeDirection rot = ForgeDirection.getOrientation(tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
		float angle = 0f;
		switch (rot)
		{
			case NORTH:
			{
				angle = 90f;
				break;
			}
			case SOUTH:
			{
				angle = 270f;
				break;
			}
			case EAST:
			{
				angle = 180f;
				break;
			}
		}
		glPushMatrix();
		glTranslated(x + 0.5, y + 1.5, z + 0.5);
		glRotatef(180f, 0f, 0f, 1f);
		glRotatef(angle, 0f, 1f, 0f);
		glEnable(GL_LIGHTING);
		MODEL.render(0.0625f);
		glPopMatrix();
	}

}