package dark.assembly.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector2;
import cpw.mods.fml.client.FMLClientHandler;
import dark.api.al.coding.ILogicTask;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.IRedirectTask;
import dark.api.al.coding.ITask;
import dark.assembly.AssemblyLine;
import dark.assembly.armbot.Program;
import dark.assembly.armbot.command.TaskEnd;
import dark.assembly.armbot.command.TaskGOTO;
import dark.assembly.armbot.command.TaskGive;
import dark.assembly.armbot.command.TaskIF;
import dark.assembly.armbot.command.TaskStart;
import dark.core.common.DarkMain;
import dark.core.interfaces.IScroll;

/** Not a gui itself but a component used to display task as a box inside of a gui
 *
 * @author DarkGuardsman */
public class GuiTaskList extends Gui implements IScroll
{
    protected IProgram program;
    protected int scroll = 0;
    private final float scale = 0.52f;

    private int color = 14737632;

    /** The string displayed on this control. */
    public String displayString;

    int x, y;

    public GuiTaskList(int x, int y)
    {
        this.x = x;
        this.y = y;

        program = new Program();
        program.setTaskAt(new Vector2(0, 0), new TaskGive());
        program.setTaskAt(new Vector2(0, 1), new TaskIF(new Vector2(0, 2), new Vector2(1, 1)));
        program.setTaskAt(new Vector2(0, 2), new TaskGive());
        program.setTaskAt(new Vector2(0, 3), new TaskGive());
        program.setTaskAt(new Vector2(0, 4), new TaskGive());
        program.setTaskAt(new Vector2(0, 5), new TaskGive());
        program.setTaskAt(new Vector2(0, 6), new TaskGive());
        program.setTaskAt(new Vector2(0, 7), new TaskGive());
        program.setTaskAt(new Vector2(0, 8), new TaskGive());
        program.setTaskAt(new Vector2(0, 9), new TaskGive());

        program.setTaskAt(new Vector2(1, 1), new TaskGive());
        program.setTaskAt(new Vector2(1, 2), new TaskIF(new Vector2(1, 3), new Vector2(2, 2)));
        program.setTaskAt(new Vector2(1, 3), new TaskGive());
        program.setTaskAt(new Vector2(1, 4), new TaskGive());
        program.setTaskAt(new Vector2(1, 5), new TaskGive());
        TaskGOTO ifEixt = new TaskGOTO();
        ifEixt.setExitPoint(0, new Vector2(0, 6));
        program.setTaskAt(new Vector2(1, 6), ifEixt);

        program.setTaskAt(new Vector2(2, 2), new TaskGive());
        program.setTaskAt(new Vector2(2, 3), new TaskGive());
        program.setTaskAt(new Vector2(2, 4), new TaskGive());
        program.setTaskAt(new Vector2(2, 5), new TaskGive());
        program.setTaskAt(new Vector2(2, 6), new TaskGive());
        program.setTaskAt(new Vector2(2, 7), new TaskGive());
        TaskGOTO ifEixt2 = new TaskGOTO();
        ifEixt2.setExitPoint(0, new Vector2(1, 8));
        program.setTaskAt(new Vector2(2, 8), ifEixt2);
        program.init(null);

    }

    public void setProgram(IProgram program)
    {
        this.program = program;
    }

    @Override
    public void scroll(int amount)
    {
        this.scroll += amount;
    }

    @Override
    public void setScroll(int length)
    {
        this.scroll = length;
    }

    @Override
    public int getScroll()
    {
        return this.scroll;
    }

    public void drawConsole()
    {
        //Draw icons for task
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ITask.TaskType.TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        for (int j = 0; j < 6; j++)
        {
            for (int i = 0; i < 7; i++)
            {
                int currentLine = i + this.scroll - 1;
                if (currentLine <= this.program.getSize().intY() + 1 && currentLine >= -1)
                {
                    ITask task = this.program.getTaskAt(new Vector2(j, currentLine));
                    if (currentLine == -1 && j == 0)
                    {
                        task = new TaskStart();
                    }
                    if (currentLine == this.program.getSize().intY() + 1 && j == 0)
                    {
                        task = new TaskEnd();
                    }
                    if (task != null && (!(task instanceof IRedirectTask) || task instanceof IRedirectTask && ((IRedirectTask) task).render()))
                    {
                        this.drawTexturedModalRect(x + (20 * j), y + (20 * i), 20 * task.getType().vv, 20 * task.getType().uu, 20, 20);
                    }
                    else
                    {
                        this.drawTexturedModalRect(x + (20 * j), y + (20 * i), 0, 40, 20, 20);
                    }
                }
            }

        }
    }

    public void mousePressed(int cx, int cz)
    {
        System.out.println("Player clicked at " + cx + "x " + cz + "z ");
        if (cx >= this.x && cz >= this.y && cx < (this.x + (20 * 7) + 10) && cz < (this.y + (20 * 6) + 10))
        {
            int row = (cz / 20)-4;
            int col = (cx / 20)-7;
            System.out.println("Player clicked at " + row + "r " + col + "c ");
            if (this.program != null)
            {
                ITask task = this.program.getTaskAt(new Vector2(col, row));
                if (task != null)
                {
                    //TODO open editing options
                    System.out.println("Player tried to edit task - " + task.getMethodName());
                }
            }
        }
    }
}
