package dark.common;

import java.awt.Color;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class CommonProxy
{
    public void preInit()
    {
        // TODO Auto-generated method stub

    }

    public void init()
    {
        // TODO Auto-generated method stub

    }

    public void postInit()
    {
        // TODO Auto-generated method stub

    }

    /** Renders a laser beam from one power to another by a set color for a set time
     *
     * @param world - world this laser is to be rendered in
     * @param position - start vector3
     * @param target - end vector3
     * @param color - color of the beam
     * @param age - life of the beam in 1/20 secs */
    public void renderBeam(World world, Vector3 position, Vector3 target, Color color, int age)
    {
    }

}
