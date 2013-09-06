package dark.assembly.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class GuiCommandList extends GuiScrollingList
{
    private ArrayList<String> commands;
    private int selIndex;
    private Minecraft mc;

    public GuiCommandList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight)
    {
        super(client, width, height, top, bottom, left, entryHeight);
        commands = new ArrayList<String>();
        selIndex = -1;
        this.mc = client;
    }

    public void setCommands(ArrayList<String> commands)
    {
        this.commands = (ArrayList<String>) commands.clone();
    }

    @Override
    protected int getSize()
    {
        return commands.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick)
    {
        selIndex = index;
    }

    @Override
    protected boolean isSelected(int index)
    {
        return selIndex == index;
    }

    @Override
    protected void drawBackground()
    {
        drawOutlineRect(this.left, this.left + this.listWidth, this.top, this.top + this.listHeight, 0, 0, 0, 0.5f, 0.5f, 0.5f);
    }

    public static void drawOutlineRect(int x1, int y1, int x2, int y2, float rR, float rG, float rB, float lR, float lG, float lB)
    {
        Tessellator tesselator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(rR, rG, rB, 1f);
        if (rR > 0 && rG > 0 && rB > 0)
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

    @Override
    protected void drawSlot(int slotID, int width, int slotY, int slotHeight, Tessellator tessellator)
    {
        if (slotID < commands.size())
        {
            String command = commands.get(slotID);
            if (isSelected(slotID))
                drawOutlineRect(this.left, this.left + width, this.top + slotY, this.top + slotY + slotHeight, -1, -1, -1, 0.5f, 0.5f, 0.5f);
            this.mc.fontRenderer.drawString(command, this.left + 4, slotY + 4, 0xAAAAAA, false);
        }
    }

}
