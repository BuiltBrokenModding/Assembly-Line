package dark.assembly.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import universalelectricity.core.vector.Vector2;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.IRedirectTask;
import dark.api.al.coding.ITask;
import dark.assembly.armbot.Program;
import dark.assembly.armbot.command.TaskDrop;
import dark.assembly.armbot.command.TaskEnd;
import dark.assembly.armbot.command.TaskGOTO;
import dark.assembly.armbot.command.TaskGive;
import dark.assembly.armbot.command.TaskGrabItem;
import dark.assembly.armbot.command.TaskIF;
import dark.assembly.armbot.command.TaskRotateTo;
import dark.assembly.armbot.command.TaskStart;
import dark.core.interfaces.IScroll;

/** Not a gui itself but a component used to display task as a box inside of a gui
 *
 * @author DarkGuardsman */
public class GuiTaskList extends Gui implements IScroll
{
    protected IProgram program;
    protected int scrollY = 0, scrollX;

    protected TileEntity entity;

    /** The string displayed on this control. */
    public String displayString;

    int xPos, yPos;
    int countX = 6, countY = 7;
    GuiEncoderCoder coder;

    public GuiTaskList(TileEntity entity, GuiEncoderCoder coder, int x, int y)
    {
        this.xPos = x;
        this.yPos = y;
        this.coder = coder;

        program = new Program();
        program.setTaskAt(0, 0, new TaskRotateTo());
        program.setTaskAt(0, 1, new TaskDrop());
        program.setTaskAt(0, 2, new TaskRotateTo());
        program.setTaskAt(0, 3, new TaskGrabItem());
        program.setTaskAt(0, 4, new TaskIF());
        program.setTaskAt(0, 5, new TaskRotateTo());
        program.setTaskAt(0, 6, new TaskGive());

        program.setTaskAt(1, 4, new TaskRotateTo());
        program.setTaskAt(1, 5, new TaskGive());
        program.setTaskAt(1, 6, new TaskGOTO(0, 6));

        if (program.getSize().intX() < (this.countX / 2))
        {
            this.scrollX = -2;
        }
        else
        {
            this.scrollX = 0;
        }

    }

    public void setProgram(IProgram program)
    {
        this.program = program;
    }

    @Override
    public void scroll(int amount)
    {
        this.scrollY += amount;
    }

    public void scrollSide(int i)
    {
        this.scrollX += i;
    }

    @Override
    public void setScroll(int length)
    {
        this.scrollY = length;
    }

    @Override
    public int getScroll()
    {
        return this.scrollY;
    }

    public void drawConsole(Minecraft mc)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        for (int colume = 0; colume < countX; colume++)
        {
            int actualCol = colume + this.scrollX;
            for (int row = 0; row < countY; row++)
            {
                int actualRow = row + this.scrollY - 1;
                if (actualRow <= this.program.getSize().intY() + 1 && actualRow >= -1)
                {
                    ITask task = this.program.getTaskAt(actualCol, actualRow);
                    if (actualRow == -1 && colume + this.scrollX - 1 == -1)
                    {
                        task = new TaskStart();
                    }
                    if (actualRow == this.program.getSize().intY() + 1 && colume == 0)
                    {
                        task = new TaskEnd();
                    }
                    if (task != null && (!(task instanceof IRedirectTask) || task instanceof IRedirectTask && ((IRedirectTask) task).render()))
                    {
                        FMLClientHandler.instance().getClient().renderEngine.bindTexture(task.getTextureSheet());
                        this.drawTexturedModalRect(xPos + (20 * colume), yPos + (20 * row), task.getTextureUV().intX(), task.getTextureUV().intY(), 20, 20);
                    }
                    else
                    {
                        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ITask.TaskType.TEXTURE);
                        this.drawTexturedModalRect(xPos + (20 * colume), yPos + (20 * row), 0, 40, 20, 20);
                    }
                }
            }

        }
    }

    protected void drawGuiContainerForegroundLayer(Minecraft mc, int cx, int cy)
    {
        ITask task = this.getTaskAt(cx, cy);
        if (task != null)
        {
            this.drawTooltip(mc, xPos - cy, yPos - cx + 10, "Task At: " + task.getMethodName());
        }

    }

    public void mousePressed(int cx, int cy)
    {
        ITask task = this.getTaskAt(cx, cy);
        if (task != null)
        {
            System.out.println("Task: " + task.getMethodName());
            FMLCommonHandler.instance().showGuiScreen(new GuiEditTask(this.coder, task));
        }
    }

    public ITask getTaskAt(int cx, int cz)
    {
        if (cx >= this.xPos && cz >= this.yPos && cx < this.xPos + (this.countX * 20) + 20 && cz < this.yPos + (this.countX * 20) + 20)
        {
            int col = ((cx - this.xPos) / 20) + this.scrollX;
            int row = ((cz - this.yPos) / 20) + this.scrollY;
            if (this.program != null)
            {
                return this.program.getTaskAt(col, row - 1);
            }
        }
        return null;
    }

    public void drawTooltip(Minecraft mc, int x, int y, String... toolTips)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        if (toolTips != null)
        {
            int var5 = 0;
            int var6;
            int var7;

            for (var6 = 0; var6 < toolTips.length; ++var6)
            {
                var7 = mc.fontRenderer.getStringWidth(toolTips[var6]);

                if (var7 > var5)
                {
                    var5 = var7;
                }
            }

            var6 = x + 12;
            var7 = y - 12;
            int var9 = 8;

            if (toolTips.length > 1)
            {
                var9 += 2 + (toolTips.length - 1) * 10;
            }

            if (y + var7 + var9 + 6 > 20)
            {
                var7 = 20 - var9 - y - 6;
            }

            this.zLevel = 300.0F;
            int var10 = -267386864;
            this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
            this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
            int var11 = 1347420415;
            int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
            this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
            this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            for (int var13 = 0; var13 < toolTips.length; ++var13)
            {
                String var14 = "\u00a77" + toolTips[var13];

                mc.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);

                if (var13 == 0)
                {
                    var7 += 2;
                }

                var7 += 10;
            }

            this.zLevel = 0.0F;
        }
    }

}
