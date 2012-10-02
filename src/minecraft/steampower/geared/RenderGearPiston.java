package steampower.geared;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import steampower.SteamPowerMain;
import steampower.turbine.TileEntitySteamPiston;

public class RenderGearPiston extends TileEntitySpecialRenderer
{
	int type = 0;
	private ModelGearPiston model;
	
	public RenderGearPiston()
	{
		model = new ModelGearPiston();
	}
	public void renderTileEntityAt(TileEntitySteamPiston tileEntity, double d, double d1, double d2, float d3) {
		bindTextureByName(SteamPowerMain.textureFile+"GearShaftPiston.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		int meta = tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		
		switch(meta)
		{
			case 1:GL11.glRotatef(0f, 0f, 1f, 0f);break;
			case 2:GL11.glRotatef(90f, 0f, 1f, 0f);break;
			case 3:GL11.glRotatef(180f, 0f, 1f, 0f);break;
			case 0:GL11.glRotatef(270f, 0f, 1f, 0f);break;
		}
		model.renderGear(0.0625F);
		model.renderR(0.0625F,tileEntity.pos);
		model.renderBody(0.0625F);
		model.renderBack(0.0625F);
		model.renderFront(0.0625F);
		model.renderLeft(0.0625F);
		model.renderRight(0.0625F);
		GL11.glPopMatrix();
	}


	@Override
	public void renderTileEntityAt(TileEntity var1, double d, double d1,
			double d2, float d3) {
		this.renderTileEntityAt(((TileEntitySteamPiston)var1), d, d1, d2, d3);
		
	}

}