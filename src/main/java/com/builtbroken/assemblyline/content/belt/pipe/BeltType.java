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
    //Normal 1 to 1 strait belt, with chest buffer in center
    BUFFER(-1);

    public final int inventorySize;

    BeltType(int inventorySize)
    {
        this.inventorySize = inventorySize;
    }
}