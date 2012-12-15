package assemblyline.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import assemblyline.client.model.ModelConveyorBelt;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.belt.BlockConveyorBelt;
import assemblyline.common.machine.belt.BlockConveyorBelt.SlantType;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderConveyorBelt extends TileEntitySpecialRenderer
{
	private ModelConveyorBelt model = new ModelConveyorBelt();

	private void renderAModelAt(TileEntityConveyorBelt tileEntity, double x, double y, double z, float f)
	{
		String flip = "";
		boolean mid = tileEntity.getIsMiddleBelt();
		SlantType slantType = BlockConveyorBelt.getSlant(tileEntity.worldObj, new Vector3(tileEntity));
		int face = tileEntity.getDirection().ordinal();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glRotatef(180f, 0f, 0f, 1f);

		this.bindTextureByName(AssemblyLine.TEXTURE_PATH + "BeltTexture" + flip + ".png");

		switch (face)
		{
			case 2:
				GL11.glRotatef(0f, 0f, 1f, 0f);
				break;
			case 3:
				GL11.glRotatef(180f, 0f, 1f, 0f);
				break;
			case 4:
				GL11.glRotatef(-90f, 0f, 1f, 0f);
				break;
			case 5:
				GL11.glRotatef(90f, 0f, 1f, 0f);
				break;
		}

		if (slantType != null)
		{
			if (slantType == SlantType.UP)
			{
				if (face == 5 || face == 4)
				{
					GL11.glTranslatef(0f, 0f, 1f);
					GL11.glRotatef(-45f, 1f, 0f, 0f);
				}
				else if (face == 2 || face == 3)
				{
					GL11.glTranslatef(0f, 0f, 1f);
					GL11.glRotatef(-45f, 1f, 0f, 0f);
				}
			}
			else if (slantType == SlantType.DOWN)
			{
				if (face == 5 || face == 4)
				{
					GL11.glTranslatef(0f, 0f, -1f);
					GL11.glRotatef(45f, 1f, 0f, 0f);
				}
				else if (face == 2 || face == 3)
				{
					GL11.glTranslatef(0f, 0f, -1f);
					GL11.glRotatef(45f, 1f, 0f, 0f);
				}
			}
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