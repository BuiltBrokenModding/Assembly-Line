package com.builtbroken.assemblyline.content.belt.pipe;

/**
 * Different type of belts support by the {@link com.builtbroken.assemblyline.content.belt.TilePipeBelt}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2017.
 */
public enum BeltType
{
    //Normal 1 to 1 strait belt
    /* 0 */NORMAL(3),

    //1 to 1 belt that turns to the left
    /* 1 */LEFT_ELBOW(3),

    //1 to 1 belt that turns to the right
    /* 2 */RIGHT_ELBOW(3),

    //3 way belt with dynamic inputs and outputs
    //User defined directions
    //Supports upgrades
    /* 3 */JUNCTION(4),

    //4 way belt with dynamic inputs and outputs
    //User defined directions
    //Supports upgrades
    /* 4 */INTERSECTION(5),

    //Splitter for use with double belt
    //Default will take a single input and split over a double
    //Can be reversed to merge a double into the same pipe
    /* 5 */SPLITTER(5),

    //Two belts in one block
    //both act independent of each other
    //Can support a strait belt or elbow for dynamic logic
    /* 6 */DOUBLE(6),

    //Half a belt basically, used as an end point for a belt.
    //Unlike normal belts can accept input from top (unless upgraded to change logic)
    //Supports upgrades (lifter, Hopper to output on bottom)
    //Un upgraded allows for user to right click add or remove items
    //Can be reversed to change direction
    /* 7 */END_CAP(2);

    /** Size of the inventory by default with no upgrades */
    public final int inventorySize;

    BeltType(int inventorySize)
    {
        this.inventorySize = inventorySize;
    }

    /**
     * Converts an index or meta value into a belt type
     *
     * @param i - index or meta value, is checked to fit into array
     * @return type or {@link #NORMAL} in place of a null or error
     */
    public static BeltType get(int i)
    {
        if (i >= 0 && i < values().length)
        {
            return values()[i];
        }
        return NORMAL;
    }

}