package assemblyline.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

/**
 * Copied from GSM lib and modified for this mod only
 * 
 * @author Rseifert
 * 
 */
@SideOnly(Side.CLIENT)
public class GuiButtonImage extends GuiButton
{
	private int type = 0;

	public GuiButtonImage(int par1, int par2, int par3, int type)
	{
		super(par1, par2, par3, 12, 12, "");
		this.type = type;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft par1Minecraft, int width, int hight)
	{
		if (this.drawButton)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture("/assemblyline/textures/gui@.png"));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean var4 = width >= this.xPosition && hight >= this.yPosition && width < this.xPosition + this.width && hight < this.yPosition + this.height;
			int var5 = 106;
			int var6 = 0;
			if (var4)
			{
				var5 += this.height;
			}

			this.drawTexturedModalRect(this.xPosition, this.yPosition, var6, var5, this.width, this.height);
		}
	}
}