package dark.core.prefab.invgui;

import universalelectricity.core.vector.Vector2;
import net.minecraft.client.gui.Gui;

/** When done should be a prefab that can be used to render a power bar on the screen
 * 
 * @author DarkGuardsman */
public class GuiPowerBar extends Gui
{
    float currentLevel = 0;
    float maxLevel = 10;

    protected Vector2 size;
    protected Vector2 position;

    public GuiPowerBar(int xx, int yy, int width, int height)
    {
        this.size = new Vector2(width, height);
        this.position = new Vector2(xx, yy);
    }
}
