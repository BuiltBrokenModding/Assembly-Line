package net.minecraft.src.eui.turbine;

import org.lwjgl.opengl.GL11;
import java.math.*;
import java.lang.Integer;
import net.minecraft.src.*;
import net.minecraft.src.universalelectricity.UniversalElectricity;

	public class GUIGenerator extends GuiContainer
	{
	    private TileEntityGenerator tileEntity;

	    private int containerWidth;
	    private int containerHeight;

	    public GUIGenerator(InventoryPlayer par1InventoryPlayer, TileEntityGenerator tileEntity)
	    {
	        super(new ContainerGenerator(par1InventoryPlayer, tileEntity));
	        this.tileEntity = tileEntity;
	    }

	    /**
	     * Draw the foreground layer for the GuiContainer (everything in front of the items)
	     */
	    protected void drawGuiContainerForegroundLayer()
	    {
	        this.fontRenderer.drawString("Steam Engine MkI", 55, 6, 4210752);
	        this.fontRenderer.drawString("MeterReadings", 90, 33, 4210752);
	        String displayText = "";
	        String displayText2 = "";
	        String displayText3 = "";
	        if(tileEntity.connectedElectricUnit == null)
	        {
	        	displayText = "Not Connected";
	        }
	        else if(tileEntity.generateRate*20 <= 0)
	        {
	        	if(tileEntity.steamStored> 0)
	        	{
	        	displayText = "Power Full";
	        	}
	        	if(tileEntity.steamStored<= 0)
	        	{
	        	displayText = "No Steam";
	        	}
	        }
	        else if(tileEntity.generateRate*20 < 20)
	        {
	        	displayText = "Warming UP: "+(int)(tileEntity.generateRate*100)+"%";
	        }
	        else
	        {
	        	displayText = UniversalElectricity.getWattDisplay((int)(tileEntity.generateRate*20));
	        }
	        	displayText2 = "water" + "-" + tileEntity.waterStored;
	        	displayText3 = "steam" + "-" + tileEntity.steamStored;
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
