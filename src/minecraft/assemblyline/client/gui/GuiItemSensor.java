package assemblyline.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import assemblyline.common.machine.sensor.ContainerItemSensor;
import assemblyline.common.machine.sensor.TileItemSensor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemSensor extends GuiContainer
{
	private EntityPlayer player;
	private GuiSmallButton invert;
	private TileItemSensor tileEntity;

	public GuiItemSensor(EntityPlayer player, TileItemSensor tileEntity)
	{
		super(new ContainerItemSensor(player.inventory, tileEntity));
		this.tileEntity = tileEntity;
		this.player = player;
		this.allowUserInput = false;
		short baseHeight = 222;
		int var4 = baseHeight - 108;
		this.ySize = var4 + 3 * 18;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		invert = new GuiSmallButton(0, this.guiLeft + 82, this.guiTop + 5, 12, 12, "i");
		controlList.add(invert);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		switch (button.id)
		{
			case 0: //invert
			{
				PacketHandler.sendTileEntityAction(player, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, PacketHandler.PACKET_ACTION_ITEM_SENSOR);
			}
		}
	}
	
	@Override
	public void updateScreen()
	{
		invert.displayString = tileEntity.isItemCheckInverted() ? "e" : "i";
		super.updateScreen();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRenderer.drawString("Item Sensor", 8, 6, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture("/gui/container.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, 3 * 18 + 17);
		this.drawTexturedModalRect(var5, var6 + 3 * 18 + 17, 0, 126, this.xSize, 96);
	}
}
