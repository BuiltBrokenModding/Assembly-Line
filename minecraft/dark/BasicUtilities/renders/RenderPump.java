package dark.BasicUtilities.renders;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.machines.TileEntityPump;


public class RenderPump extends TileEntitySpecialRenderer
{
	int type = 0;
	private ModelPump model;
	
	public RenderPump()
	{
		model = new ModelPump();
	}

	public void renderAModelAt(TileEntityPump tileEntity, double d, double d1, double d2, float f)
	{
       Liquid type = tileEntity.type;
       int meta = tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
       switch(type.ordinal())
       {
       		default: bindTextureByName(BasicUtilitiesMain.textureFile+"pumps/Pump.png");break;
       		case 1:bindTextureByName(BasicUtilitiesMain.textureFile+"pumps/WaterPump.png");break;//water
       		case 2:bindTextureByName(BasicUtilitiesMain.textureFile+"pumps/LavaPump.png");break;//lava
       		case 3:bindTextureByName(BasicUtilitiesMain.textureFile+"pumps/OilPump.png");break;//oil
       		//case 4://fuel
       }
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		switch(meta)
		{
			case 1:GL11.glRotatef(0f, 0f, 1f, 0f);break;
			case 2:GL11.glRotatef(90f, 0f, 1f, 0f);break;
			case 3:GL11.glRotatef(180f, 0f, 1f, 0f);break;
			case 0:GL11.glRotatef(270f, 0f, 1f, 0f);break;
		}
		model.renderMain(0.0625F);
		model.renderC1(0.0625F);
		model.renderC2(0.0625F);
		model.renderC3(0.0625F);
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) {
		this.renderAModelAt((TileEntityPump)tileEntity, var2, var4, var6, var8);
	}

}