package dark.fluid.client.render.pipe;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.fluid.client.model.ModelLargePipe;
import dark.fluid.common.pipes.TileEntityPipe;

public class RenderPipeWindow implements IPipeExtentionRender
{
	private ModelLargePipe SixPipe;

	public RenderPipeWindow()
	{
		SixPipe = new ModelLargePipe();
	}

	@Override
	public void renderAModelAt(RenderPipe renderPipe, TileEntityPipe pipe, Vector3 location, float size, ForgeDirection facingDirection)
	{
		renderPipe.bindTextureForPipe(RenderPipe.getPipeTexture(0, false));
		this.render(facingDirection.ordinal());
		System.out.println("Rendered Window Pipe");

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