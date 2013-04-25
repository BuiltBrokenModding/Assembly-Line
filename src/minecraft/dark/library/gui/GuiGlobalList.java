package dark.library.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringTranslate;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import universalelectricity.prefab.GuiBase;
import dark.library.DarkMain;

public class GuiGlobalList extends GuiBase
{
	EntityPlayer player;
	private GuiTextField stringInput;
	int xSize;
	int ySize;
	public GuiGlobalList(EntityPlayer player) {
		super();
		this.player = player;
		this.xSize = 256;
		this.ySize = 226;
	}

	@Override
	protected void keyTyped(char character, int keycode)
	{
		if (keycode == Keyboard.KEY_ESCAPE)
		{
			this.mc.thePlayer.closeScreen();
		}
		this.stringInput.textboxKeyTyped(character, keycode);
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		StringTranslate var1 = StringTranslate.getInstance();
		int width = (this.width - this.xSize) / 2;
		int height = (this.height - this.ySize) / 2;

		this.stringInput = new GuiTextField(this.fontRenderer, width + 12, height + 165, 135, 11);
		this.stringInput.setMaxStringLength(30);

		this.buttonList.add(new GuiButtonArrow(1, width + 151, height + 21, false));
		this.buttonList.add(new GuiButtonArrow(2, width + 151, height + 152, true));
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.stringInput.setFocused(true);
	}

	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);
	}	

	@Override
	protected void mouseClicked(int x, int y, int type)
	{
		super.mouseClicked(x, y, type);

		this.stringInput.mouseClicked(x, y, type);
	}

	@Override
	protected void drawForegroundLayer(int x, int y, float var1)
	{
		String title = "Global Access Interface";
		this.fontRenderer.drawString("\u00a77" + title, this.xSize / 2 - title.length() * 3, 4, 4210752);
		
	}

	@Override
	protected void drawBackgroundLayer(int x, int y, float var1)
	{
		this.mc.renderEngine.bindTexture(DarkMain.GUI_DIRECTORY + "gui_access_base.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int containerWidth = (this.width - this.xSize) / 2;
		int containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
		
	}
}
