package dark.library.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringTranslate;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.GuiBase;
import universalelectricity.prefab.vector.Region2;
import dark.library.DarkMain;
import dark.library.access.UserAccess;
import dark.library.access.interfaces.IScroll;

public class GuiGlobalList extends GuiBase implements IScroll
{
	EntityPlayer player;
	private GuiTextField stringInput;
	private int scroll = 0;
	private static final int SPACING = 10;
	private final HashMap<Object, Vector2> outputMap = new HashMap<Object, Vector2>();

	public GuiGlobalList(EntityPlayer player)
	{
		super();
		this.player = player;
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
		if (type == 0)
		{
			Iterator<Entry<Object, Vector2>> it = this.outputMap.entrySet().iterator();

			while (it.hasNext())
			{
				Entry<Object, Vector2> entry = it.next();
				Vector2 minPos = entry.getValue();
				minPos.x -= 2;
				minPos.y -= 2;
				Vector2 maxPos = minPos.clone();
				maxPos.x += 132;
				maxPos.y += SPACING + 2;

				if (new Region2(minPos, maxPos).isIn(new Vector2(x - this.guiLeft, y - this.guiTop)))
				{
					this.clickedEntry(entry.getKey());
				}
			}
		}
		this.stringInput.mouseClicked(x, y, type);
	}

	public void clickedEntry(Object entry)
	{

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

	public void drawConsole(int x, int y, int lines)
	{
		int color = 14737632;
		outputMap.clear();

		// Draws Each Line
		for (int i = 0; i < lines; i++)
		{
			int currentLine = i + this.getScroll();

			if (currentLine < this.getListLength() && currentLine >= 0)
			{
				Object object = getDisplayList().get(currentLine);
				String line = "-----";
				if (object instanceof UserAccess)
				{
					UserAccess accesInfo = (UserAccess) object;
					line = accesInfo.username + " (" + accesInfo.level.displayName + ")";
				}
				else if (object instanceof String)
				{
					String accesInfo = (String) object;
					line = "List: " + accesInfo;
				}

				if (line != null && line != "")
				{
					Vector2 drawPosition = new Vector2(x, SPACING * i + y);
					outputMap.put(object, drawPosition);
					this.fontRenderer.drawString(line, drawPosition.intX(), drawPosition.intY(), color);
				}
			}
		}
	}

	public int getListLength()
	{
		return 10;
	}

	public List getDisplayList()
	{
		return new ArrayList();
	}

	@Override
	public void scroll(int amount)
	{
		this.setScroll(this.scroll + amount);
	}

	@Override
	public void setScroll(int length)
	{
		this.scroll = Math.max(Math.min(length, this.getListLength()), 0);
	}

	@Override
	public int getScroll()
	{
		return this.scroll;
	}
}
