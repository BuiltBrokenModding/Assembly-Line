package net.minecraft.src.Utility;

import org.lwjgl.opengl.GL11;
import java.math.*;
import java.lang.Integer;
import net.minecraft.src.*;
import net.minecraft.src.universalelectricity.UniversalElectricity;
import net.minecraft.src.universalelectricity.components.ContainerCoalGenerator;
import net.minecraft.src.universalelectricity.components.TileEntityCoalGenerator;

	public class GUIBoost extends GuiContainer
	{
	    private TileEntityBoost tileEntity;

	    private int containerWidth;
	    private int containerHeight;

	    public GUIBoost(InventoryPlayer par1InventoryPlayer, TileEntityBoost tileEntity)
	    {
	        super(new ContainerBoost(par1InventoryPlayer, tileEntity));
	        this.tileEntity = tileEntity;
	    }

	    /**
	     * Draw the foreground layer for the GuiContainer (everything in front of the items)
	     */
	    protected void drawGuiContainerForegroundLayer()
	    {
	        this.fontRenderer.drawString("Doc-o-Matic", 55, 6, 4210752);
	        this.fontRenderer.drawString("MeterReadings", 90, 33, 4210752);
	        String displayText = "";
	        String displayText2 = "";
	        String displayText3 = "";
	        	        	displayText2 = "Energy" + "-" + tileEntity.eStored;
	        	displayText3 = "Effects" + "-" + tileEntity.hStored;
	        this.fontRenderer.drawString(displayText, (int)(105-displayText.length()*1), 45, 4210752);
	        this.fontRenderer.drawString(displayText2, (int)(105-displayText.length()*1), 55, 4210752);
	        this.fontRenderer.drawString(displayText3, (int)(105-displayText.length()*1), 65, 4210752);
	        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	    }

	    /**
	     * Draw the background layer for the GuiContainer (everything behind the items)
	     */
	    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	    {
	        int var4 = this.mc.renderEngine.getTexture("/eui/SteamGUI.png");
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        this.mc.renderEngine.bindTexture(var4);
	        containerWidth = (this.width - this.xSize) / 2;
	        containerHeight = (this.height - this.ySize) / 2;
	        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	    }
	}
