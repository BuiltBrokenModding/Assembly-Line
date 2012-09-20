package steampower;
import java.text.DecimalFormat;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

import basicpipes.pipes.api.Liquid;

import steampower.boiler.ContainerBoiler;
import steampower.boiler.TileEntityBoiler;

public class GuiBoiler extends GuiContainer
{
    private TileEntityBoiler boilerInventory;

    public GuiBoiler(InventoryPlayer par1InventoryPlayer, TileEntityBoiler par2TileEntityGrinder)
    {
        super(new ContainerBoiler(par1InventoryPlayer, par2TileEntityGrinder));
        this.boilerInventory = par2TileEntityGrinder;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everythin in front of the items)
     */
    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString("Boiler", 60, 6, 4210752);
        this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752); 
        if(boilerInventory.hullHeat >=10000)
        {
        	//this.fontRenderer.drawString("Heat Danger", (int)(105), 50, 4210752);
        }
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture(SteamPowerMain.textureFile+"BoilerGui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize-1, this.ySize);
        int var7;
        int var8;
        int var9;
        int var10;
        if (this.boilerInventory.waterStored > 0)
        {
            var7 = boilerInventory.getStoredLiquid(Liquid.WATER)*4 + 1;
            this.drawTexturedModalRect(var5 + 29, var6 + 72 - var7, 176, 148 - var7, 23, var7);
        }
        if (this.boilerInventory.steamStored > 0)
        {
            var8 = boilerInventory.steamStored/14*4 + 1;
            this.drawTexturedModalRect(var5 + 108, var6 + 72 - var8, 176, 90 - var8, 23, var8);
        }
       
        	float precentH = Math.min(boilerInventory.hullHeat/1000 + 1, 10);
            var9 = (int) Math.min(precentH*3.0F,30);
            this.drawTexturedModalRect(var5 + 59, var6 + 70 - var9, 199, 71 - var9, 9, var9);
            float precentSH = this.boilerInventory.heatStored/1000;
            var10 = (int) Math.round(precentSH*5.33);
            this.drawTexturedModalRect(var5 + 78, var6 + 16, 176, 14, var10, 16);
        
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
   			displayWatt = watts+" kJ";
   		}
   		
   		return displayWatt;
   	}
       public static double roundTwoDecimals(double d)
       {
   		DecimalFormat twoDForm = new DecimalFormat("#.##");
   		return Double.valueOf(twoDForm.format(d));
       }
}
