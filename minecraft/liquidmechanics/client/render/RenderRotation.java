package liquidmechanics.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.common.ForgeDirection;

public class RenderRotation
{
    float angle;
    float x;
    float y;
    float z;
    public RenderRotation(float angle, float x, float y, float z)
    {
        this.angle = angle;
        this.x = x;
        this.y = y;
        this.z = z;
        
    }
}
