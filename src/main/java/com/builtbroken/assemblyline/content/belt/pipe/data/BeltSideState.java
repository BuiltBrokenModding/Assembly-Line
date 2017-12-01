package com.builtbroken.assemblyline.content.belt.pipe.data;

import com.builtbroken.mc.api.ISave;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Used for define the state of a side of the belt
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2017.
 */
public class BeltSideState implements ISave
{
    /** Inventory slot this represents */
    public final int slotID;
    /** Side of the block, point away from the tile */
    public final ForgeDirection side;
    /** Can the state be modified */
    public final boolean canBeConfigured;
    /** Is state setup to output items from the belt */
    public boolean output;
    /** Filter to use for allowing items into this side, only works for input */
    public BeltInventoryFilter filter; //TODO implement simple filter object


    public BeltSideState(int slot, ForgeDirection side, boolean canBeConfigured, boolean output)
    {
        this.slotID = slot;
        this.side = side;
        this.canBeConfigured = canBeConfigured;
        this.output = output;
    }

    @Override
    public void load(NBTTagCompound tag)
    {
        //Prevent loading to avoid mistakes
        if (canBeConfigured)
        {
            output = tag.getBoolean("output");
        }

        //TODO load filter
    }

    @Override
    public NBTTagCompound save(NBTTagCompound tag)
    {
        tag.setBoolean("output", output);
        //TODO save filter
        return tag;
    }

    public BeltSideState copy(boolean b)
    {
        return new BeltSideState(slotID, side, b, output);
    }
}
