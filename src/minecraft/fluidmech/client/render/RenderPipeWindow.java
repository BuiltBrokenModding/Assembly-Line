package fluidmech.client.render;

import hydraulic.api.ColorCode;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import fluidmech.client.model.ModelLargePipe;
import fluidmech.client.render.pipeextentions.IPipeExtentionRender;
import fluidmech.common.FluidMech;
import fluidmech.common.machines.pipes.IPipeExtention;
import fluidmech.common.machines.pipes.TileEntityPipe;
import fluidmech.common.machines.pipes.TileEntityPipe;

public class RenderPipeWindow implements IPipeExtentionRender
{
	private ModelLargePipe SixPipe;
	private boolean[] renderSide = new boolean[6];

	public RenderPipeWindow()
	{
		SixPipe = new ModelLargePipe();
	}

	public void renderAModelAt(TileEntityPipe pipe, double d, double d1, double d2, float size, ForgeDirection facingDirection)
	{
		// Texture file
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.1F, -1.1F, -1.1F);
		this.render(facingDirection.ordinal());
		GL11.glPopMatrix();

	}

	public void render(int side)
	{
		switch (side)
		{
			case 0:
				SixPipe.renderBottom();
				break;
			case 1:
				SixPipe.renderTop();
				break;
			case 3:
				SixPipe.renderFront();
				break;
			case 2:
				SixPipe.renderBack();
				break;
			case 5:
				SixPipe.renderRight();
				break;
			case 4:
				SixPipe.renderLeft();
				break;
		}
	}

}