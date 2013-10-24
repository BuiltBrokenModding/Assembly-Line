package dark.assembly.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.IRedirectTask;
import dark.api.al.coding.ITask;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.armbot.Program;
import dark.assembly.common.armbot.command.TaskGive;
import dark.core.interfaces.IScroll;

public class GuiTaskList extends Gui implements IScroll
{
    protected IProgram program;
    protected int scroll = 0;

    /** The x position of this control. */
    public int xPosition;

    /** The y position of this control. */
    public int yPosition;

    /** The string displayed on this control. */
    public String displayString;

    public static final ResourceLocation TEXTURE_PROCESS = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "PROCESS.png");

    public GuiTaskList(int x, int y)
    {
        this.xPosition = x;
        this.yPosition = y;
        program = new Program();
        program.setTaskAt(new Vector2(0, 0), new TaskGive());
        program.setTaskAt(new Vector2(0, 1), new TaskGive());
        program.setTaskAt(new Vector2(0, 2), new TaskGive());
        program.setTaskAt(new Vector2(0, 3), new TaskGive());
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

    public void drawConsole(FontRenderer fontRenderer)
    {
        int spacing = 10;
        int color = 14737632;

        GL11.glPushMatrix();
        float scale = 0.92f;
        GL11.glScalef(scale, scale, scale);

        // Draws each line
        for (int i = 0; i < 4; i++)
        {
            int currentLine = i + this.scroll;

            if (currentLine < this.program.getSize().intY() && currentLine >= 0)
            {
                ITask task = this.program.getTaskAt(new Vector2(0, currentLine));

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
                            uu = 50;
                            vv = 0;
                            break;
                    }
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.TEXTURE_PROCESS);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.drawTexturedModalRect(this.xPosition + xx / 2, this.yPosition + (yy * i), uu, vv, xx, yy);
                    this.drawCenteredString(fontRenderer, task.getMethodName(), this.xPosition + xx, this.yPosition + (yy * i), -6250336);
                }
            }

        }

        GL11.glPopMatrix();
    }
}
