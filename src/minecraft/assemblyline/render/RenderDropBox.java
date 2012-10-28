package assemblyline.render;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

import assemblyline.AssemblyLine;
import assemblyline.interaction.TileEntityInjector;
import assemblyline.model.ModelDropBox;

public class RenderDropBox extends TileEntitySpecialRenderer
{
	private ModelDropBox model = new ModelDropBox();

	public void renderAModelAt(TileEntityInjector tileEntity, double x, double y, double z, float f)
	{
		int face = tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);		
        bindTextureByName(AssemblyLine.TEXTURE_PATH+"DropBox.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		
		switch(face)
		{
			case 4: GL11.glRotatef(180f, 0f, 1f, 0f); break;
			case 6: GL11.glRotatef(0f, 0f, 1f, 0f); break;
			case 5: GL11.glRotatef(270f, 0f, 1f, 0f); break;
			case 7:  GL11.glRotatef(90f, 0f, 1f, 0f); break;
		}
		System.out.println("RENDERIN DROP BOX");
		model.render(0.0625F);
		GL11.glPopMatrix();
	
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8) 
	{
		this.renderAModelAt((TileEntityInjector)tileEntity, var2, var4, var6, var8);
	}

}