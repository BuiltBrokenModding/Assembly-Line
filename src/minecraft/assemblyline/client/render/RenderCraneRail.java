package assemblyline.client.render;

import static org.lwjgl.opengl.GL11.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import assemblyline.client.model.ModelCraneRail;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.crane.CraneHelper;
import assemblyline.common.machine.crane.TileEntityCraneRail;

public class RenderCraneRail extends RenderImprintable
{
	public static final String TEXTURE = "craneRail.png";
	public static final ModelCraneRail MODEL = new ModelCraneRail();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
	{
		if (tileEntity != null && tileEntity instanceof TileEntityCraneRail)
		{
			int tX, tY, tZ;
			tX = tileEntity.xCoord;
			tY = tileEntity.yCoord;
			tZ = tileEntity.zCoord;
			boolean renderUp = CraneHelper.canFrameConnectTo(tileEntity, tX, tY + 1, tZ, ForgeDirection.DOWN);
			boolean renderDown = CraneHelper.canFrameConnectTo(tileEntity, tX, tY - 1, tZ, ForgeDirection.UP);
			//EAST, X-
			boolean renderLeft = CraneHelper.canFrameConnectTo(tileEntity, tX - 1, tY, tZ, ForgeDirection.EAST);
			//WAST, X+
			boolean renderRight = CraneHelper.canFrameConnectTo(tileEntity, tX + 1, tY, tZ, ForgeDirection.WEST);
			//SOUTH, Z-
			boolean renderFront = CraneHelper.canFrameConnectTo(tileEntity, tX, tY, tZ - 1, ForgeDirection.SOUTH);
			//NORTH, Z+
			boolean renderBack = CraneHelper.canFrameConnectTo(tileEntity, tX, tY, tZ + 1, ForgeDirection.NORTH);
			boolean renderFoot = tileEntity.worldObj.isBlockSolidOnSide(tX, tY - 1, tZ, ForgeDirection.UP);
			this.bindTextureByName(AssemblyLine.TEXTURE_PATH + TEXTURE);
			glPushMatrix();
			glTranslated(x + 0.5, y + 1.5, z + 0.5);
			glRotatef(180f, 0f, 0f, 1f);
			glEnable(GL_LIGHTING);
			MODEL.render(renderUp, renderDown && !renderFoot, renderLeft, renderRight, renderFront, renderBack, renderFoot);
			glPopMatrix();
		}
	}

}