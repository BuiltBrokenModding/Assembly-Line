package liquidmechanics.common.handlers;

import liquidmechanics.api.helpers.ColorCode;
import liquidmechanics.api.liquids.LiquidHandler;
import liquidmechanics.common.tileentity.TileEntityPipe;
import liquidmechanics.common.tileentity.TileEntityTank;
import net.minecraft.nbt.NBTTagCompound;

/**
 * used to help convert older system to newer systems.
 */
public class UpdateConverter
{
    public static void convert(TileEntityPipe pipe, NBTTagCompound nbt)
    {
        Boolean converted24 = nbt.getBoolean("converted");
        Boolean converted25 = nbt.getBoolean("converted025");
        if (!converted24)
        {
            pipe.setColor(ColorCode.get(LiquidHandler.getFromMeta(nbt.getInteger("type"))));
        }
        else if (converted24 && !converted25)
        {
            pipe.setColor(ColorCode.get(LiquidHandler.get(nbt.getString("name"))));
        }
        nbt.setBoolean("converted", true);
        nbt.setBoolean("converted025", true);
    }
    public static void convert(TileEntityTank pipe, NBTTagCompound nbt)
    {
        Boolean converted24 = nbt.getBoolean("converted");
        Boolean converted25 = nbt.getBoolean("converted025");
        if (!converted24)
        {
            pipe.setColor(ColorCode.get(LiquidHandler.getFromMeta(nbt.getInteger("type"))));
        }
        else if (converted24 && !converted25)
        {
            pipe.setColor(ColorCode.get(LiquidHandler.get(nbt.getString("name"))));
        }
        nbt.setBoolean("converted", true);
        nbt.setBoolean("converted025", true);
    }
}
