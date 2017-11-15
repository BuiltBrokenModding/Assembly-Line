package com.builtbroken.assemblyline.content.belt.pipe.data;

import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Used for {@link TilePipeBelt#getInputs()} & {@link TilePipeBelt#getOutputs()}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2017.
 */
public class BeltSlotState
{
    public final int slotID;
    //Side pointing out of the tile
    public final ForgeDirection side;

    public BeltSlotState(int slot, ForgeDirection side)
    {
        this.slotID = slot;
        this.side = side;
    }
}
