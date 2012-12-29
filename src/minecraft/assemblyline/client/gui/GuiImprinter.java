package assemblyline.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.imprinter.ContainerImprinter;

public class GuiImprinter extends GuiContainer
{
	private int containerWidth;
	private int containerHeight;

	public GuiImprinter(InventoryPlayer par1InventoryPlayer, World worldObj, Vector3 position)
	{
		super(new ContainerImprinter(par1InventoryPlayer, worldObj, position));
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRenderer.drawString(TranslationHelper.getLocal("tile.imprinter.name"), 68, 6, 4210752);
		this.fontRenderer.drawString(TranslationHelper.getLocal("assemblyline.gui.crafting") + ":", 25, 55, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture(AssemblyLine.TEXTURE_PATH + "gui_stamper.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}
}
