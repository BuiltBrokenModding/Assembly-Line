package dark.assembly.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.client.model.ModelAngledBelt;
import dark.assembly.client.model.ModelConveyorBelt;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.machine.belt.TileEntityConveyorBelt;
import dark.assembly.common.machine.belt.TileEntityConveyorBelt.SlantType;

@SideOnly(Side.CLIENT)
public class RenderConveyorBelt extends TileEntitySpecialRenderer
{
	public static final ModelConveyorBelt MODEL = new ModelConveyorBelt();
	public static final ModelAngledBelt MODEL2 = new ModelAngledBelt();

	private void renderAModelAt(TileEntityConveyorBelt tileEntity, double x, double y, double z, float f)
	{
		boolean mid = tileEntity.getIsMiddleBelt();
		SlantType slantType = tileEntity.getSlant();
		int face = tileEntity.getDirection().ordinal();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glRotatef(180f, 0f, 0f, 1f);

		int frame = tileEntity.getAnimationFrame();

		if (slantType != null && slantType != SlantType.NONE)
		{
			switch (face)
			{
				case 2:
					GL11.glRotatef(180f, 0f, 1f, 0f);
					break;
				case 3:
					GL11.glRotatef(0f, 0f, 1f, 0f);
					break;
				case 4:
					GL11.glRotatef(90f, 0f, 1f, 0f);
					break;
				case 5:
					GL11.glRotatef(-90f, 0f, 1f, 0f);
					break;
			}

			if (slantType == SlantType.UP)
			{
				ResourceLocation name = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + "slantedbelt/frame" + frame + ".png");
				func_110628_a(name);

				GL11.glTranslatef(0f, 0.8f, -0.8f);
				GL11.glRotatef(180f, 0f, 1f, 1f);
				boolean slantAdjust = false;
				TileEntity test = tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord + tileEntity.getDirection().offsetX, tileEntity.yCoord, tileEntity.zCoord + tileEntity.getDirection().offsetZ);
				if (test != null)
				{
					if (test instanceof TileEntityConveyorBelt)
					{
						if (((TileEntityConveyorBelt) test).getSlant() == SlantType.TOP)
						{
							GL11.glRotatef(10f, 1f, 0f, 0f);
							slantAdjust = true;
						}
					}
				}
				MODEL2.render(0.0625F, true);
			}
			else if (slantType == SlantType.DOWN)
			{
				ResourceLocation name = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + "slantedbelt/frame" + frame + ".png");
				func_110628_a(name);
				GL11.glRotatef(180f, 0f, 1f, 0f);
				boolean slantAdjust = false;
				TileEntity test = tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord - tileEntity.getDirection().offsetX, tileEntity.yCoord, tileEntity.zCoord - tileEntity.getDirection().offsetZ);
				if (test != null)
				{
					if (test instanceof TileEntityConveyorBelt)
					{
						if (((TileEntityConveyorBelt) test).getSlant() == SlantType.TOP)
						{
							GL11.glRotatef(-10f, 1f, 0f, 0f);
							GL11.glTranslatef(0f, 0.25f, 0f);
							slantAdjust = true;
						}
					}
				}
				MODEL2.render(0.0625F, slantAdjust);
			}
			else
			{
				ResourceLocation name = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + "belt/frame" + frame + ".png");
				func_110628_a(name);
				GL11.glRotatef(180, 0f, 1f, 0f);
				GL11.glTranslatef(0f, -0.68f, 0f);
				MODEL.render(0.0625f, (float) Math.toRadians(tileEntity.wheelRotation), tileEntity.getIsLastBelt(), tileEntity.getIsFirstBelt(), false, false);
			}
		}
		else
		{
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
			ResourceLocation name = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + "belt/frame" + frame + ".png");
			func_110628_a(name);
			MODEL.render(0.0625F, (float) Math.toRadians(tileEntity.wheelRotation), tileEntity.getIsLastBelt(), tileEntity.getIsFirstBelt(), false, true);

		}

		int ent = tileEntity.worldObj.getBlockId(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);

		GL11.glPopMatrix();

	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
	{
		this.renderAModelAt((TileEntityConveyorBelt) tileEntity, var2, var4, var6, var8);
	}

}