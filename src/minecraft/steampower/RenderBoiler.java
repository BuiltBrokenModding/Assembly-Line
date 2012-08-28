package steampower;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import steampower.boiler.TileEntityBoiler;

public class RenderBoiler extends TileEntitySpecialRenderer
{
	int type = 0;
	private ModelTank model;
	private ModelCenterTank model2;
	private ModelCornerTank model3;
	
	public RenderBoiler(float par1)
	{
		model = new ModelTank(par1);
		model2 = new ModelCenterTank(par1);
		model3 = new ModelCornerTank(par1);
	}


	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double d, double d1, double d2, float d3) {
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		TileEntity[] connected = ((TileEntityBoiler)tileEntity).connectedBlocks;
		int meta = 0;
		if(connected[5] == null && connected[3] == null && connected[4] == null && connected[2] == null || ((TileEntityBoiler)tileEntity).tankCount < 2 )
		{
			bindTextureByName(SteamPowerMain.textureFile+"tankTexture.png");
			model.generalRender(0.0625F);
		}
		else 
		if(TradeHelper.corner(tileEntity) == 0 || ((TileEntityBoiler)tileEntity).tankCount > 2)
		{
			bindTextureByName(SteamPowerMain.textureFile+"tankBlock.png");
			model2.renderBlock(0.0625F);
		}
		else
		{
				int corner =  TradeHelper.corner(tileEntity);
				bindTextureByName(SteamPowerMain.textureFile+"CornerTank.png");
				switch(corner)
				{
				case 1: GL11.glRotatef(270f, 0f, 1f, 0f);break;
				case 2: GL11.glRotatef(0f, 0f, 1f, 0f);break;
				case 3: GL11.glRotatef(90f, 0f, 1f, 0f);break;
				case 4: GL11.glRotatef(180f, 0f, 1f, 0f);break;
				}
				model3.renderCorner(0.0625f);
			
		}
		GL11.glPopMatrix();
	}

}