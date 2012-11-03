package assemblyline.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import assemblyline.AssemblyLine;
import assemblyline.machines.ContainerSorter;
import assemblyline.machines.TileEntitySorter;

public class GuiSorter extends GuiContainer
{
	private TileEntitySorter tileEntity;

	private int containerWidth;
	private int containerHeight;

	public GuiSorter(InventoryPlayer par1InventoryPlayer, TileEntitySorter tileEntity)
	{
		super(new ContainerSorter(par1InventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	public void initGui()
	{
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		int wid = (this.width - this.xSize) / 2;
		int hig = (this.height - this.ySize) / 2;
		this.controlList.add(new GuiButton(0, wid + 112, hig + 32, 44, 19, "Toggle"));
		for (int i = 1; i < this.tileEntity.guiButtons.length; i++)
		{
			this.controlList.add(new GuiButtonImage(i, wid + 17 + i * 18, hig + 17, 0));
		}
	}

	public void updateScreen()
	{
		super.updateScreen();
	}

	/**
	 * Fired when a control is clicked. This is
	 * the equivalent of
	 * ActionListener.actionPerformed(ActionEvent
	 * e).
	 */
	protected void actionPerformed(GuiButton button)
	{
		if (button.id < 5)
		{
			this.tileEntity.changeOnOff(button.id);
		}
		super.actionPerformed(button);
	}

	protected void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
	}

	public void onGuiClosed()
	{
		super.onGuiClosed();
	}

	/**
	 * Draw the foreground layer for the
	 * GuiContainer (everything in front of the
	 * items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRenderer.drawString(this.tileEntity.getInvName(), 55, 6, 4210752);
		this.fontRenderer.drawString("Voltage: " + (int) this.tileEntity.getVoltage(), 95, 60, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Draw the background layer for the
	 * GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture(AssemblyLine.TEXTURE_PATH + "gui_ejector.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);

		// GUI button changes
		for (int i = 1; i < this.tileEntity.guiButtons.length; i++)
		{
			this.drawTexturedModalRect(containerWidth + 17 + i * 18, containerHeight + 17, 176, +(tileEntity.guiButtons[i] ? 12 : 0), 12, 12);
		}
		this.fontRenderer.drawString((tileEntity.guiButtons[0] ? "Inv" : "Other"), containerWidth + 108, containerHeight + 22, 4210752);
	}
}
