package dark.assembly.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.AssemblyLine;
import dark.assembly.machine.encoder.ContainerEncoder;
import dark.assembly.machine.encoder.TileEntityEncoder;

@SideOnly(Side.CLIENT)
public class GuiEncoderInventory extends GuiEncoderBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "gui_encoder_slot.png");

    public GuiEncoderInventory(InventoryPlayer inventoryPlayer, TileEntityEncoder tileEntity)
    {
        super(inventoryPlayer, tileEntity, new ContainerEncoder(inventoryPlayer, tileEntity));
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(par1, x, y);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int containerWidth = (this.width - this.xSize) / 2;
        int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
}
