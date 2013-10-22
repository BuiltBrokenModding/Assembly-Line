package com.builtbroken.common;

/** Simple class uses to plot a vector on a line
 * 
 * @author Robert seifert */
public class Vector
{
    protected float x;

    public Vector(float x)
    {
        this.x = x;
    }

    public int X()
    {
        return (int) x;
    }

    public float XX()
    {
        return x;
    }

    /** Magnitude between two points */
    public float mag(Vector vec)
    {
        return vec.x - x;
    }

    /** Distance to another point */
    public float distance(Vector vec)
    {
        return Math.abs(mag(vec));
    }

    @Override
    public String toString()
    {
        return super.toString() + "[" + x + "x]";
    }
}
