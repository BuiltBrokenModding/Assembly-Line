package basicpipes;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import basicpipes.conductors.TileEntityPipe;
import basicpipes.machines.TileEntityPump;
import basicpipes.pipes.api.Liquid;


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
       		default: bindTextureByName("/textures/pumps/Pump.png");break;
       		//case 0://steam
       		case 1:bindTextureByName("/textures/pumps/WaterPump.png");break;//water
       		case 2:bindTextureByName("/textures/pumps/LavaPump.png");break;//lava
       		case 3:bindTextureByName("/textures/pumps/OilPump.png");break;//oil
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