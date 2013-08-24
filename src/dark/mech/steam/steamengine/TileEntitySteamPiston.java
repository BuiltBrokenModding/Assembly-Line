package dark.mech.steam.steamengine;

import dark.core.blocks.TileEntityMachine;
import dark.mech.steam.SteamPowerMain;

public class TileEntitySteamPiston extends TileEntityMachine
{

    @Override
    public String getChannel()
    {
        return SteamPowerMain.CHANNEL;
    }
}
