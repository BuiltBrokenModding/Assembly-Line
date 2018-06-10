package com.builtbroken.assemblyline.content.belt.pipe.data;

import com.builtbroken.mc.data.Direction;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Used for define the state of a side of the belt
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2017.
 */
public class BeltSideState
{
    /** Inventory slot this represents */
    public final int slotID;
    /** Side of the block, point away from the tile */
    public final ForgeDirection side;
    /** Side of the block, point away from the tile */
    public final Direction direction;
    /** Is state setup to output items from the belt */
    public final boolean output;


    public BeltSideState(int slot, Direction side, boolean output)
    {
        this.slotID = slot;
        this.side = ForgeDirection.getOrientation(side.ordinal());
        this.direction = side;
        this.output = output;
    }
}
