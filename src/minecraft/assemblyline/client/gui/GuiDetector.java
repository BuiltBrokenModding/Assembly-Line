package assemblyline.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import assemblyline.common.machine.detector.ContainerDetector;
import assemblyline.common.machine.detector.TileEntityDetector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Briman0094
 */
@SideOnly(Side.CLIENT)
public class GuiDetector extends GuiContainer
{
	private TileEntityDetector tileEntity;

	public GuiDetector(InventoryPlayer inventory, TileEntityDetector tileEntity)
	{
		super(new ContainerDetector(inventory, tileEntity));
		this.tileEntity = tileEntity;
		this.allowUserInput = false;
		short baseHeight = 222;
		int var4 = baseHeight - 108;
		this.ySize = var4 + 3 * 18;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRenderer.drawString(this.tileEntity.getInvName(), 8, 6, 4210752);
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
