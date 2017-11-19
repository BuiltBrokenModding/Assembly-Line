package com.builtbroken.assemblyline.content.belt.pipe;

public enum BeltType
{
    //Normal 1 to 1 strait belt
    NORMAL(3),
    //1 to 1 belt that turns to the left
    LEFT_ELBOW(3),
    //1 to 1 belt that turns to the left
    RIGHT_ELBOW(3),
    //3 way belt with dynamic inputs and outputs
    T_SECTION(4),
    //4 way belt with dynamic inputs and outputs
    INTERSECTION(5),
    //Splitter for use with double belt
    Y_SECTION(5),
    //Two belts in one block, both act independent of each other
    DOUBLE(6);

    public final int inventorySize;

    BeltType(int inventorySize)
    {
        this.inventorySize = inventorySize;
    }

    public static BeltType get(int i)
    {
        if(i >= 0 && i < values().length)
        {
            return values()[i];
        }
        return NORMAL;
    }

}