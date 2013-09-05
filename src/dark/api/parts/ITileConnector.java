package dark.api.parts;

import net.minecraftforge.common.ForgeDirection;

public interface ITileConnector
{
    /** Can this tile connect on the given side */
    public boolean canTileConnect(Connection type, ForgeDirection dir);

    public static enum Connection
    {
        Eletricity(),
        FLUIDS(),
        FORCE(),
        PRESSURE(),
        ITEMS(),
        DATA(),
        TILE(),
        NETWORK();
    }
}
