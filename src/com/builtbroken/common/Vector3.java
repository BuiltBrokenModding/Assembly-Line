package com.builtbroken.common;

/** Simple class to plot a vector in a 3D space
 *
 * @author Robert Seifert */
public class Vector3 extends Vector2
{
    protected float z;

    public Vector3(float x, float y, float z)
    {
        super(x, y);
        this.z = z;
    }

    public int Z()
    {
        return (int) z;
    }

    public float ZZ()
    {
        return z;
    }

    @Override
    public String toString()
    {
        return super.toString() +"["+z+"z]";
    }
}
