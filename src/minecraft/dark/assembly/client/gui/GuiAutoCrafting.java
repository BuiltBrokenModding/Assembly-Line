package dark.assembly.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.common.AssemblyLine;

@SideOnly(Side.CLIENT)
public class GuiAutoCrafting extends GuiContainer
{
	private static final ResourceLocation gui_pic = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "gui_crafting.png");

	public GuiAutoCrafting(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
	{
		super(new ContainerWorkbench(par1InventoryPlayer, par2World, par3, par4, par5));
	}

	/** Draw the foreground layer for the GuiContainer (everything in front of the items) */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRenderer.drawString(StatCollector.translateToLocal("AutoCrafter"), 28, 6, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	/** Draw the background layer for the GuiContainer (everything behind the items) */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		this.mc.func_110434_K().func_110577_a(gui_pic);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
	}
}
