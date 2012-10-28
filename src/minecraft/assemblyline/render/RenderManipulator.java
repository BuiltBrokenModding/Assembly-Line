package assemblyline.render;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import assemblyline.AssemblyLine;
import assemblyline.belts.TileEntityConveyorBelt;
import assemblyline.interaction.TileEntityManipulator;
import assemblyline.model.ModelManipulator;

public class RenderManipulator extends TileEntitySpecialRenderer
{
	private ModelManipulator model = new ModelManipulator();

	public void renderAModelAt(TileEntityManipulator tileEntity, double x, double y, double z, float f)
	{
		String flip = "";//if(tileEntity.flip){flip = "F";}
		int face = tileEntity.getBeltDirection();
        
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glRotatef(180f, 0f, 0f, 1f);
		
		this.bindTextureByName(AssemblyLine.TEXTURE_PATH+"injector.png");
		
		
			if(face==2){ GL11.glRotatef(0f, 0f, 1f, 0f);}
			if(face==3){ GL11.glRotatef(180f, 0f, 1f, 0f);}
			if(face==4){ GL11.glRotatef(270f, 0f, 1f, 0f);}
			if(face==5){ GL11.glRotatef(90f, 0f, 1f, 0f);}
			int ent = tileEntity.worldObj.getBlockId(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
			model.render(0.0625F, true, 0);
			//TODO change the true part to check if there is a TE on the input side
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) 
	{
		this.renderAModelAt((TileEntityManipulator)tileEntity, var2, var4, var6, var8);
	}

}