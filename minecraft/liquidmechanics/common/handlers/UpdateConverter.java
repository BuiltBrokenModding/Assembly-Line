package liquidmechanics.common.handlers;

import net.minecraft.nbt.NBTTagCompound;
import liquidmechanics.api.helpers.Colors;
import liquidmechanics.common.tileentity.TileEntityPipe;

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
            pipe.setColor(Colors.get(LiquidHandler.getFromMeta(nbt.getInteger("type"))));
        }
        else if (converted24 && !converted25)
        {
            pipe.setColor(Colors.get(LiquidHandler.get(nbt.getString("name"))));
        }
        nbt.setBoolean("converted", true);
        nbt.setBoolean("converted025", true);
    }

}
