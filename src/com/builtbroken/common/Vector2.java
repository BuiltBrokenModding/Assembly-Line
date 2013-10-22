package com.builtbroken.common;

/** Simple class to plot a vector on a plane
 * 
 * @author Robert Seifert */
public class Vector2 extends Vector
{
    protected float y;

    public Vector2(float x, float y)
    {
        super(x);
        this.y = y;
    }

    public int Y()
    {
        return (int) y;
    }

    public float YY()
    {
        return y;
    }

    @Override
    public String toString()
    {
        return super.toString() + "[" + y + "y]";
    }
}
