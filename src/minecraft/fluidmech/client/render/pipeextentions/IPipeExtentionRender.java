package fluidmech.client.render.pipeextentions;

import fluidmech.common.machines.pipes.TileEntityPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

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
	 * process threw the RenderPipe.class
	 * 
	 * @param pipe - TileEntity this extension is attached too
	 * @param xPos yPos zPos position too be rendered from the players plane
	 * @param size - This should be the size of the render, correct me if wrong
	 * @param facingDirection - Facing direction of the extension in relation to its pipe frame
	 */
	public void renderAModelAt(TileEntityPipe pipe, double xPos, double yPos, double zPos, float size, ForgeDirection facingDirection);
}
