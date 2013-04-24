package dark.library.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;

public class GuiGlobalList extends GuiScreen
{
	@Override
	protected void keyTyped(char keycode, int par2)
	{
		if (keycode == Keyboard.KEY_ESCAPE)
		{
			this.mc.thePlayer.closeScreen();
		}
	}
}
