package net.minecraft.src.eui.burner;

import org.lwjgl.opengl.GL11;
import java.math.*;
import java.text.DecimalFormat;
import java.lang.Integer;
import net.minecraft.src.*;
import net.minecraft.src.universalelectricity.UniversalElectricity;

public class GUIFireBox extends GuiContainer
{
    private TileEntityFireBox tileEntity;

    private int containerWidth;
    private int containerHeight;

    public GUIFireBox(InventoryPlayer par1InventoryPlayer, TileEntityFireBox tileEntity)
    {
        super(new ContainerFireBox(par1InventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString("FireBox", 55, 6, 4210752);
        this.fontRenderer.drawString("HeatOut", 90, 33, 4210752);
        String displayText = "";
        if(!tileEntity.isConnected)
        {
        	displayText = "No Boiler";
        }
        else if(tileEntity.containingItems[0] != null)
        {
        	if(tileEntity.containingItems[0].getItem().shiftedIndex != Item.coal.shiftedIndex)
	        {
	        	displayText = "No Coal";
	        }
	        else{ 	        	
	        if(tileEntity.generateRate*20 < 20)
	        {
	        	displayText = "Hull Heat: "+(tileEntity.generateRate*100)+"%";
	        }
	        else
	        {
	        	displayText = getWattDisplay((tileEntity.generateRate*20));
	        }
	        }
        }
        this.fontRenderer.drawString(displayText, (int)(105-displayText.length()*1.25), 45, 4210752);
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
    public static String getWattDisplay(int watts)
	{
		String displayWatt;
		if(watts > 1000)
		{
			displayWatt = roundTwoDecimals((double)watts/1000)+" MJ";
		}
		else
		{
			displayWatt = watts+" KJ";
		}
		
		return displayWatt;
	}
    public static double roundTwoDecimals(double d)
    {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
    }
}
