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
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.armbot.Command;
import assemblyline.common.machine.encoder.ContainerEncoder;
import assemblyline.common.machine.encoder.IInventoryWatcher;
import assemblyline.common.machine.encoder.ItemDisk;
import assemblyline.common.machine.encoder.TileEntityEncoder;

public class GuiEncoder extends GuiContainer implements IInventoryWatcher
{
	private int containerWidth;
	private int containerHeight;
	private TileEntityEncoder tileEntity;
	private ArrayList<String> commands;

	// list stuff
	private int minCommand;

	private GuiButton addButton;
	private GuiButton delButton;
	private GuiButton pUpButton;
	private GuiButton pDnButton;
	private GuiTextField commandField;

	public GuiEncoder(InventoryPlayer par1InventoryPlayer, World worldObj, TileEntityEncoder tileEntity)
	{
		super(new ContainerEncoder(par1InventoryPlayer, worldObj, tileEntity));
		this.ySize = 256;
		this.tileEntity = tileEntity;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.allowUserInput = true;

		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;

		this.addButton = new GuiButton(0, containerWidth + (xSize - 25), containerHeight + 128, 18, 20, "+");
		this.delButton = new GuiButton(1, containerWidth + (xSize - 43), containerHeight + 128, 18, 20, "-");
		this.pUpButton = new GuiButton(2, containerWidth + (xSize - 25), containerHeight + 48, 18, 20, "");
		this.pDnButton = new GuiButton(3, containerWidth + (xSize - 25), containerHeight + 108, 18, 20, "");
		this.commandField = new GuiTextField(fontRenderer, 8, 129, xSize - 52, 18);
		// commandList = new GuiCommandList(mc, xSize - 7, 128, 7, 120, 170, 20);

		this.controlList.add(addButton);
		this.controlList.add(delButton);
		this.controlList.add(pUpButton);
		this.controlList.add(pDnButton);

		this.minCommand = 0;
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
							PacketDispatcher.sendPacketToServer(PacketManager.getPacket(AssemblyLine.CHANNEL, this.tileEntity, (String) this.commandField.getText(), true));
						}
					}

					this.commandField.setText("");
				}

				break;
			}
			case 1: // subtract
			{
				if (!this.commandField.getText().equals(""))
				{
					if (this.tileEntity != null)
					{
						ItemStack disk = this.tileEntity.getStackInSlot(0);

						if (disk != null && Command.getCommand(this.commandField.getText()) != null)
						{
							ArrayList<String> tempCmds = ItemDisk.getCommands(disk);
							tempCmds.remove(commandField.getText());
							ItemDisk.setCommands(disk, tempCmds);
							this.tileEntity.setInventorySlotContents(0, disk);
							PacketDispatcher.sendPacketToServer(PacketManager.getPacket(AssemblyLine.CHANNEL, this.tileEntity, (String) this.commandField.getText(), false));
						}
					}

					this.commandField.setText("");
				}

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
		this.fontRenderer.drawString(TranslationHelper.getLocal("tile.encoder.name"), 68, 6, 4210752);
		this.fontRenderer.drawString("Disk:", 56, 28, 4210752);

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
		for (int i = minCommand; i < minCommand + 8; i++)
		{

		}
	}

	private void drawCommand(String command, int x, int y)
	{

	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		commandField.mouseClicked(x - containerWidth, y - containerHeight, button);
	}

	@Override
	protected void keyTyped(char character, int keycode)
	{
		if (keycode == Keyboard.KEY_ESCAPE)
		{
			mc.thePlayer.closeScreen();
		}
		else
		{
			commandField.textboxKeyTyped(character, keycode);
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

		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
		drawOutlineRect(containerWidth + 7, containerHeight + 48, containerWidth + (xSize - 25), containerHeight + 48 + 80, 0, 0, 0, 0.5f, 0.5f, 0.5f);
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
		if (tileEntity != null)
		{
			ItemStack disk = tileEntity.getStackInSlot(0);
			if (disk != null)
			{
				commands = ItemDisk.getCommands(disk);
			}
		}
		minCommand = 0;
	}

	@Override
	public void inventoryChanged()
	{
		this.updateCommands();
	}
}
