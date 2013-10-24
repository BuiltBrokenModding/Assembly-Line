package dark.assembly.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
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

/** Not a gui itself but a component used to display task as a box inside of a gui
 *
 * @author DarkGuardsman */
public class GuiTaskList extends Gui implements IScroll
{
    protected IProgram program;
    protected int scroll = 0;

    /** The string displayed on this control. */
    public String displayString;

    public static final ResourceLocation TEXTURE_PROCESS = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "PROCESS.png");

    public GuiTaskList()
    {
        program = new Program();
        program.setTaskAt(new Vector2(0, 0), new TaskGive());
        program.setTaskAt(new Vector2(0, 1), new TaskGive());
        program.setTaskAt(new Vector2(0, 2), new TaskGive());
        program.setTaskAt(new Vector2(0, 3), new TaskGive());
        program.setTaskAt(new Vector2(0, 4), new TaskGive());
        program.setTaskAt(new Vector2(0, 5), new TaskGive());
        program.setTaskAt(new Vector2(0, 6), new TaskGive());
        program.setTaskAt(new Vector2(0, 7), new TaskGive());
        program.setTaskAt(new Vector2(0, 8), new TaskGive());
        program.setTaskAt(new Vector2(0, 9), new TaskGive());
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
        int spacingY = 25;
        int spacingX = 25;
        int color = 14737632;

        GL11.glPushMatrix();
        //With everything scaled the gui will not align like a normal one so use a scaled distance from the main GUI
        float scale = 0.52f;
        ScaledResolution scaledresolution = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight);
        int scaleH = scaledresolution.getScaledHeight();
        int scaleW = scaledresolution.getScaledWidth();
        this.drawCenteredString(minecraft.fontRenderer, "Scale - " + scaleW + "x " + scaleH + "y", 100, 100, color);
        int desiredH = 240;
        int desiredW = 427;
        float sh = (scaleH / desiredH) / scale;
        float sW = (scaleW / desiredW) / scale;
        spacingY = (int) (spacingY * sh);
        spacingX = (int) (spacingX * sW);
        //Start drawing after everying is scaled down
        GL11.glScalef(scale, scale, scale);

        //TODO add zooming which will involve storing scales with distance translations factors
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 6; i++)
            {
                int currentLine = i + this.scroll;

                if (currentLine <= this.program.getSize().intY() && currentLine >= 0)
                {
                    ITask task = this.program.getTaskAt(new Vector2(j, currentLine));

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
                        Vector2 center = new Vector2(x  * sW + (spacingX * j), y * sh + (spacingY * i));
                        this.drawTexturedModalRect(center.intX(), center.intY(), uu, vv, xx, yy);
                        this.drawCenteredString(minecraft.fontRenderer, task.getMethodName(), center.intX() + (xx / 2), center.intY() + ((yy - 8) / 2), color);
                    }
                }
            }

        }

        GL11.glPopMatrix();
    }
}
