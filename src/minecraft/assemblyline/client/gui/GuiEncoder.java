package assemblyline.client.gui;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.command.Command;
import assemblyline.common.machine.encoder.ContainerEncoder;
import assemblyline.common.machine.encoder.IInventoryWatcher;
import assemblyline.common.machine.encoder.ItemDisk;
import assemblyline.common.machine.encoder.TileEntityEncoder;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiEncoder extends GuiContainer implements IInventoryWatcher
{
	private static final int MAX_COMMANDS = 6;

	private int containerWidth;
	private int containerHeight;
	private TileEntityEncoder tileEntity;
	private ArrayList<String> commands;

	// list stuff
	private int minCommand;
	private int selCommand;

	private GuiButton addButton;
	private GuiButton delButton;
	private GuiButton pUpButton;
	private GuiButton pDnButton;
	private GuiTextField commandField;

	public GuiEncoder(InventoryPlayer playerInventory, TileEntityEncoder tileEntity)
	{
		super(new ContainerEncoder(playerInventory, tileEntity));
		this.ySize = 256;
		this.tileEntity = tileEntity;
		tileEntity.setWatcher(this);
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.allowUserInput = true;
		Keyboard.enableRepeatEvents(true);

		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;

		this.addButton = new GuiButton(0, containerWidth + (xSize - 25), containerHeight + 128 + ContainerEncoder.Y_OFFSET, 18, 20, "+");
		this.delButton = new GuiButton(1, containerWidth + (xSize - 43), containerHeight + 128 + ContainerEncoder.Y_OFFSET, 18, 20, "-");
		this.pUpButton = new GuiButton(2, containerWidth + (xSize - 25), containerHeight + 46 + ContainerEncoder.Y_OFFSET, 18, 20, "");
		this.pDnButton = new GuiButton(3, containerWidth + (xSize - 25), containerHeight + 106 + ContainerEncoder.Y_OFFSET, 18, 20, "");
		this.commandField = new GuiTextField(fontRenderer, 8, 129 + ContainerEncoder.Y_OFFSET, xSize - 52, 18);
		// commandList = new GuiCommandList(mc, xSize - 7, 128, 7, 120, 170, 20);

		this.controlList.add(addButton);
		this.controlList.add(delButton);
		this.controlList.add(pUpButton);
		this.controlList.add(pDnButton);

		this.commands = new ArrayList<String>();
		this.minCommand = 0;
		this.selCommand = -1;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		switch (button.id)
		{
			case 0: // add
			{
				if (!this.commandField.getText().equals(""))
				{
					if (this.tileEntity != null)
					{
						ItemStack disk = this.tileEntity.getStackInSlot(0);

						if (disk != null && Command.getCommand(this.commandField.getText()) != null)
						{
							ArrayList<String> tempCmds = ItemDisk.getCommands(disk);
							tempCmds.add(commandField.getText());
							ItemDisk.setCommands(disk, tempCmds);
							this.tileEntity.setInventorySlotContents(0, disk);
							PacketDispatcher.sendPacketToServer(PacketManager.getPacket(AssemblyLine.CHANNEL, this.tileEntity, true, (String) this.commandField.getText()));
						}
					}

					this.minCommand = this.commands.size() - MAX_COMMANDS + 1;
					if (this.minCommand < 0)
						this.minCommand = 0;
					this.selCommand = -1;
					this.commandField.setText("");
				}

				break;
			}
			case 1: // remove
			{
				if (this.tileEntity != null)
				{
					ItemStack disk = this.tileEntity.getStackInSlot(0);

					if (disk != null && this.selCommand >= 0 && this.selCommand < this.commands.size())
					{
						ArrayList<String> tempCmds = ItemDisk.getCommands(disk);
						tempCmds.remove(this.selCommand);
						ItemDisk.setCommands(disk, tempCmds);
						this.tileEntity.setInventorySlotContents(0, disk);
						PacketDispatcher.sendPacketToServer(PacketManager.getPacket(AssemblyLine.CHANNEL, this.tileEntity, false, this.selCommand));
					}
					
					this.selCommand = -1;
				}

				break;
			}
			case 2: // page up
			{
				if (minCommand > 0)
					minCommand--;
				break;
			}
			case 3: // page down
			{
				if (minCommand + MAX_COMMANDS < this.commands.size())
					minCommand++;
				break;
			}
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		glColor4f(1, 1, 1, 1);
		glDisable(GL_LIGHTING);
		this.fontRenderer.drawString(TranslationHelper.getLocal("tile.encoder.name"), 68, 8 + ContainerEncoder.Y_OFFSET, 4210752);
		this.fontRenderer.drawString("Disk:", 56, 28 + ContainerEncoder.Y_OFFSET, 4210752);

		// render page up and page down buttons
		glPushMatrix();
		glTranslatef(pUpButton.xPosition - containerWidth + 6, pUpButton.yPosition - containerHeight + 7, 0);
		this.fontRenderer.drawString("^", 1, 1, 0x444444);
		this.fontRenderer.drawString("^", 0, 0, 0xFFFFFF);
		glPopMatrix();
		glPushMatrix();
		glTranslatef(pDnButton.xPosition - containerWidth + 6, pDnButton.yPosition - containerHeight + 7, 0);
		glRotatef(180, 0, 0, 1);
		glTranslatef(-5, -4, 0);
		this.fontRenderer.drawString("^", -1, -1, 0x444444);
		this.fontRenderer.drawString("^", 0, 0, 0xFFFFFF);
		glPopMatrix();

		if (commands != null)
		{
			drawCommands();
		}

		commandField.drawTextBox();
	}

	private void drawCommands()
	{
		int relativeCommand;
		String command;
		for (int i = minCommand; i < minCommand + MAX_COMMANDS; i++)
		{
			if (i >= 0 && i < this.commands.size())
			{
				relativeCommand = i - minCommand;
				command = this.commands.get(i).toUpperCase();
				drawCommand(command, 8, 56 + relativeCommand * (fontRenderer.FONT_HEIGHT + 4), this.selCommand == i);
			}
		}
	}

	private void drawCommand(String command, int x, int y, boolean selected)
	{
		if (selected)
		{
			drawOutlineRect(x, y, x + 142, y + fontRenderer.FONT_HEIGHT + 4, 0, 0, 0, 1f, 1f, 1f);
		}
		this.fontRenderer.drawString(command, x + 3, y + (fontRenderer.FONT_HEIGHT / 2) - 1, 0xFFFFFF, false);
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		this.commandField.mouseClicked(x - containerWidth, y - containerHeight, button);
		if (button == 0)
		{
			if (x >= containerWidth + 8)
			{
				if (y >= containerHeight + 47 + ContainerEncoder.Y_OFFSET)
				{
					if (x <= containerWidth + (xSize - 25))
					{
						if (y <= containerHeight + 46 + 80 + ContainerEncoder.Y_OFFSET)
						{
							listClicked(x - (containerWidth + 8), y - (containerHeight + 47 + ContainerEncoder.Y_OFFSET));
						}
						else
						{
							selCommand = -1;
						}
					}
					else
					{
						selCommand = -1;
					}
				}
				else
				{
					selCommand = -1;
				}
			}
			else
			{
				selCommand = -1;
			}
		}
	}

	private void listClicked(int relativeX, int relativeY)
	{
		int itemClicked = relativeY / 13;
		this.selCommand = itemClicked + this.minCommand;
	}

	@Override
	protected void keyTyped(char character, int keycode)
	{
		if (character != 'e' && character != 'E') // don't close GUI
			super.keyTyped(character, keycode);
		commandField.textboxKeyTyped(character, keycode);
		// System.out.println(keycode);
		if (keycode == Keyboard.KEY_ESCAPE)
		{
			this.mc.thePlayer.closeScreen();
		}
		else if (keycode == Keyboard.KEY_RETURN)
		{
			if (this.commandField.isFocused())
			{
				actionPerformed(this.addButton);
			}
		}
		else if (keycode == Keyboard.KEY_DELETE)
		{
			actionPerformed(this.delButton);
		}
		else if (keycode == 201) // PAGE UP (no constant)
		{
			actionPerformed(this.pUpButton);
		}
		else if (keycode == 209) // PAGE DOWN (no constant)
		{
			actionPerformed(this.pDnButton);
		}
		else if (keycode == Keyboard.KEY_UP)
		{
			this.selCommand--;
			if (this.selCommand < -1)
				this.selCommand = this.commands.size() - 1;
			if (this.selCommand < minCommand)
				if (this.selCommand >= 0)
					this.minCommand = selCommand;
			if (this.selCommand >= this.minCommand + MAX_COMMANDS)
				if (this.selCommand < this.commands.size())
					this.minCommand = this.selCommand - MAX_COMMANDS + 1;
		}
		else if (keycode == Keyboard.KEY_DOWN)
		{
			this.selCommand++;
			if (this.selCommand >= this.commands.size())
				this.selCommand = -1;
			if (this.selCommand >= this.minCommand + MAX_COMMANDS)
				if (this.selCommand < this.commands.size())
					this.minCommand = this.selCommand - MAX_COMMANDS + 1;
			if (this.selCommand < minCommand)
				if (this.selCommand >= 0)
					this.minCommand = selCommand;
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture(AssemblyLine.TEXTURE_PATH + "gui_encoder.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);

		this.drawTexturedModalRect(containerWidth, containerHeight + ContainerEncoder.Y_OFFSET, 0, 0, this.xSize, this.ySize);
		drawOutlineRect(containerWidth + 7, containerHeight + 46 + ContainerEncoder.Y_OFFSET, containerWidth + (xSize - 25), containerHeight + 46 + 80 + ContainerEncoder.Y_OFFSET, 0, 0, 0, 0.5f, 0.5f, 0.5f);
	}

	public static void drawOutlineRect(int x1, int y1, int x2, int y2, float rR, float rG, float rB, float lR, float lG, float lB)
	{
		Tessellator tesselator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(rR, rG, rB, 1f);
		if (rR >= 0 && rG >= 0 && rB >= 0)
		{
			// background
			tesselator.startDrawingQuads();
			tesselator.addVertex((double) x1, (double) y2, 0.0D);
			tesselator.addVertex((double) x2, (double) y2, 0.0D);
			tesselator.addVertex((double) x2, (double) y1, 0.0D);
			tesselator.addVertex((double) x1, (double) y1, 0.0D);
			tesselator.draw();
		}
		// outline
		GL11.glColor4f(lR, lG, lB, 1f);
		tesselator.startDrawingQuads();
		tesselator.addVertex((double) x1, (double) y1, 0.0D);
		tesselator.addVertex((double) x1, (double) y2, 0.0D);
		tesselator.addVertex((double) x1 + 1, (double) y2, 0.0D);
		tesselator.addVertex((double) x1 + 1, (double) y1, 0.0D);
		tesselator.draw();
		tesselator.startDrawingQuads();
		tesselator.addVertex((double) x2 - 1, (double) y1, 0.0D);
		tesselator.addVertex((double) x2 - 1, (double) y2, 0.0D);
		tesselator.addVertex((double) x2, (double) y2, 0.0D);
		tesselator.addVertex((double) x2, (double) y1, 0.0D);
		tesselator.draw();
		tesselator.startDrawingQuads();
		tesselator.addVertex((double) x1, (double) y1, 0.0D);
		tesselator.addVertex((double) x1, (double) y1 + 1, 0.0D);
		tesselator.addVertex((double) x2, (double) y1 + 1, 0.0D);
		tesselator.addVertex((double) x2, (double) y1, 0.0D);
		tesselator.draw();
		tesselator.startDrawingQuads();
		tesselator.addVertex((double) x1, (double) y2 - 1, 0.0D);
		tesselator.addVertex((double) x1, (double) y2, 0.0D);
		tesselator.addVertex((double) x2, (double) y2, 0.0D);
		tesselator.addVertex((double) x2, (double) y2 - 1, 0.0D);
		tesselator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void updateCommands()
	{
		if (commands != null)
		{
			commands.clear();
			if (tileEntity != null)
			{
				ItemStack disk = tileEntity.getStackInSlot(0);
				if (disk != null)
				{
					commands = ItemDisk.getCommands(disk);
				}
			}
			if (this.minCommand + MAX_COMMANDS >= this.commands.size())
				this.minCommand = this.commands.size() - MAX_COMMANDS;
			if (this.minCommand < 0)
				this.minCommand = 0;
		}
	}
	
	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
	

	@Override
	public void inventoryChanged()
	{
		this.updateCommands();
	}
}
