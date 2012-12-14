package assemblyline.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import assemblyline.client.model.ModelConveyorBelt;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;
@SideOnly(Side.CLIENT)
public class RenderConveyorBelt extends TileEntitySpecialRenderer
{
	private ModelConveyorBelt model = new ModelConveyorBelt();

	private void renderAModelAt(TileEntityConveyorBelt tileEntity, double x, double y, double z, float f)
	{
		String flip = "";// if(tileEntity.flip){flip
							// = "F";}
		boolean mid = tileEntity.getIsMiddleBelt();
		int face = tileEntity.getBeltDirection();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glRotatef(180f, 0f, 0f, 1f);

		bindTextureByName(AssemblyLine.TEXTURE_PATH + "BeltTexture" + flip + ".png");
		if (face == 2)
		{
			GL11.glRotatef(180f, 0f, 1f, 0f);
		}
		else if (face == 3)
		{
			GL11.glRotatef(0f, 0f, 1f, 0f);
		}
		else if (face == 4)
		{
			GL11.glRotatef(90f, 0f, 1f, 0f);
		}
		else if (face == 5)
		{
			GL11.glRotatef(270f, 0f, 1f, 0f);
		}
		int ent = tileEntity.worldObj.getBlockId(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		model.render(0.0625F, (float) Math.toRadians(tileEntity.wheelRotation), tileEntity.getIsBackCap(), tileEntity.getIsFrontCap(), false);

		GL11.glPopMatrix();

	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
	{
		this.renderAModelAt((TileEntityConveyorBelt) tileEntity, var2, var4, var6, var8);
	}

}