package dark.BasicUtilities.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;

public interface IReadOut
{
    /**
     * Grabs the message displayed to the user
     * on right click of the machine with the
     * pipe gauge
     * @param user
     * @param side - may not work correctly yet but should give you a side
     * @return - a string to be displayed to the player for a reading. automatically adds ReadOut: to the beginning
     */
    public String getMeterReading(EntityPlayer user, ForgeDirection side);
}
