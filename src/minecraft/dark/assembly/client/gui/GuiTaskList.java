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
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.armbot.Program;
import dark.assembly.common.armbot.command.TaskEnd;
import dark.assembly.common.armbot.command.TaskGOTO;
import dark.assembly.common.armbot.command.TaskGive;
import dark.assembly.common.armbot.command.TaskIF;
import dark.assembly.common.armbot.command.TaskStart;
import dark.core.interfaces.IScroll;

/** Not a gui itself but a component used to display task as a box inside of a gui
 *
 * @author DarkGuardsman */
public class GuiTaskList extends Gui implements IScroll
{
    protected IProgram program;
    protected int scroll = 0;

    private final int desiredH = 240;
    private final int desiredW = 427;
    private final float scale = 0.52f;

    private int color = 14737632;

    /** The string displayed on this control. */
    public String displayString;

    public static final ResourceLocation TEXTURE_PROCESS = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "PROCESS.png");

    public GuiTaskList()
    {
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

    public void drawConsole(Minecraft minecraft, int x, int y)
    {
        GL11.glPushMatrix();
        //With everything scaled the gui will not align like a normal one so use a scaled distance from the main GUI
        ScaledResolution scaledresolution = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight);
        int scaleH = scaledresolution.getScaledHeight();
        int scaleW = scaledresolution.getScaledWidth();
        //this.drawCenteredString(minecraft.fontRenderer, "Scale - " + scaleW + "x " + scaleH + "y", 100, 100, color);

        float sh = (scaleH / desiredH) / scale;
        float sW = (scaleW / desiredW) / scale;
        int spacingY = (int) (40 * sh);
        int spacingX = (int) (40 * sW);

        //Start drawing after everying is scaled down
        GL11.glScalef(scale, scale, scale);

        //Draw lines between tasks
        color = Color.BLUE.getRGB();
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 4; i++)
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
                    if (task != null)
                    {
                        if (task instanceof IRedirectTask && !((IRedirectTask) task).render())
                        {
                            continue;
                        }
                        int xx = 50;
                        int yy = 39;
                        int uu = 0;
                        int vv = 0;
                        switch (task.getType())
                        {
                            case DATA:
                                break;
                            case PROCESS:
                                break;
                            case DEFINEDPROCESS:
                                xx = 50;
                                yy = 39;
                                uu = 0;
                                vv = 39;
                                break;
                            case DECISION:
                                xx = 50;
                                yy = 50;
                                uu = 0;
                                vv = 78;
                                break;
                            case START:
                            case END:
                                xx = 39;
                                yy = 28;
                                uu = 0;
                                vv = 128;
                                break;
                        }
                        Vector2 center = new Vector2(((x + 35) - xx) * sW + (spacingX / 2), (y + 25 - (yy / 2)) * sh + (spacingY * i));
                        if (task instanceof ILogicTask)
                        {
                            for (Vector2 vec : ((ILogicTask) task).getExits())
                            {
                                //Task must be close so not to waste a shit ton of line rendering, as well it needs to stay on screen, and only be one column over
                                if (vec.distanceTo(task.getPosition()) < 5)
                                {
                                    if (vec.x >= task.getPosition().x)
                                    {
                                        this.drawHorizontalLine(center.intX(), center.intY(), center.intX() + (spacingX / 2), color);
                                        if (vec.y >= task.getPosition().y)
                                        {
                                            this.drawVerticalLine(center.intX() + (spacingX / 2), center.intY(), center.intY() + (spacingY * (vec.intY() - task.getPosition().intY())), color);
                                        }
                                        else if (vec.y < task.getPosition().y)
                                        {
                                            this.drawVerticalLine(center.intX() + (spacingX / 2), center.intY(), center.intY() - (spacingY * (task.getPosition().intY() - vec.intY())), color);
                                        }
                                    }
                                    else if (vec.x < task.getPosition().x)
                                    {
                                        this.drawHorizontalLine(center.intX(), center.intY(), center.intX() - (spacingX / 2), color);
                                        if (vec.y >= task.getPosition().y)
                                        {
                                            this.drawVerticalLine(center.intX() - (spacingX / 2), center.intY(), center.intY() + (spacingY * (vec.intY() - task.getPosition().intY())), color);
                                        }
                                        else if (vec.y < task.getPosition().y)
                                        {
                                            this.drawVerticalLine(center.intX() - (spacingX / 2), center.intY(), center.intY() - (spacingY * (task.getPosition().intY() - vec.intY())), color);
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            this.drawVerticalLine(center.intX(), center.intY(), center.intY() + spacingY, color);
                        }
                    }
                }
            }

        }
        //Draw icons for task
        color = 14737632;
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 4; i++)
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
                    if (task != null)
                    {
                        if (task instanceof IRedirectTask && !((IRedirectTask) task).render())
                        {
                            continue;
                        }
                        int xx = 50;
                        int yy = 39;
                        int uu = 0;
                        int vv = 0;
                        switch (task.getType())
                        {
                            case DATA:
                                break;
                            case PROCESS:
                                break;
                            case DEFINEDPROCESS:
                                xx = 50;
                                yy = 39;
                                uu = 0;
                                vv = 39;
                                break;
                            case DECISION:
                                xx = 50;
                                yy = 50;
                                uu = 0;
                                vv = 78;
                                break;
                            case START:
                            case END:
                                xx = 39;
                                yy = 28;
                                uu = 0;
                                vv = 128;
                                break;
                        }
                        FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.TEXTURE_PROCESS);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        Vector2 center = new Vector2((x + 35 - (xx / 2)) * sW + (spacingX * j), (y + 25 - (yy / 2)) * sh + (spacingY * i));
                        this.drawTexturedModalRect(center.intX(), center.intY(), uu, vv, xx, yy);
                        this.drawCenteredString(minecraft.fontRenderer, task.getMethodName(), center.intX() + (xx / 2), center.intY() + ((yy - 8) / 2), color);
                    }
                }
            }

        }

        GL11.glPopMatrix();
    }
}
