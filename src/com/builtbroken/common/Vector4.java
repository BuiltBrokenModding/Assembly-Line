package com.builtbroken.common;

/** class used to plot a point in a 3D space over time. Also used for ploting a vector in a 4D space
 * 
 * @author Robert seifert */
public class Vector4 extends Vector3
{
    protected float time;

    public Vector4(float x, float y, float z, float time)
    {
        super(x, y, z);
        this.time = time;
    }

    public float time()
    {
        return time;
    }

    @Override
    public String toString()
    {
        return super.toString() + "[" + time + "t]";
    }
}
