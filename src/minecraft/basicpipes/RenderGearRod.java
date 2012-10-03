package basicpipes;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import steampower.geared.ModelGearPiston;

import basicpipes.conductors.TileEntityPipe;
import basicpipes.conductors.TileEntityRod;
import basicpipes.machines.TileEntityPump;
import basicpipes.pipes.api.Liquid;


public class RenderGearRod extends TileEntitySpecialRenderer
{
	private ModelGearRod model;
	public RenderGearRod()
	{
		model = new ModelGearRod();
	}
	public void renderAModelAt(TileEntityRod tileEntity, double d, double d1, double d2, float f)
	{
        bindTextureByName("/textures/GearRod.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		int meta = tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		switch(meta)
		{
			case 0: GL11.glRotatef(90f, 1f, 0f, 0f);break;
			case 1: GL11.glRotatef(-90f, 1f, 0f, 0f);break;
			case 2:GL11.glRotatef(0f, 0f, 1f, 0f);break;
			case 5:GL11.glRotatef(90f, 0f, 1f, 0f);break;
			case 3:GL11.glRotatef(180f, 0f, 1f, 0f);break;
			case 4:GL11.glRotatef(270f, 0f, 1f, 0f);break;
		}
		model.render(0.0625F,tileEntity.pos);
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) {
		this.renderAModelAt((TileEntityRod)tileEntity, var2, var4, var6, var8);
	}

}