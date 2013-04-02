package fluidmech.client.render.pipeextentions;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import fluidmech.common.machines.pipes.TileEntityPipe;

/**
 * Class for TileEntity Renders that extend the pipe class to use instead of extending
 * TileEntitySpecialRender
 * 
 * @author Rseifert
 * 
 */
public interface IPipeExtentionRender
{
	/**
	 * Renders the pipe extension just like a normal tileEntity render however this is called and
	 * process threw the RenderPipe.class so you don't need to do all the GL11 calls for scaling,
	 * translation, etc
	 * 
	 * @param renderPipe - render instance that is calling this method
	 * @param pipe - TileEntity this extension is attached too
	 * @param location - position too be rendered from the players plane
	 * @param size - This should be the size of the render, correct me if wrong
	 * @param facingDirection - Facing direction of the extension in relation to its pipe frame
	 */
	public void renderAModelAt(RenderPipe renderPipe, TileEntityPipe pipe, Vector3 location, float size, ForgeDirection facingDirection);
}
