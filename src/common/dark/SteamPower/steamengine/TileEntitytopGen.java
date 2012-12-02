package dark.SteamPower.steamengine;

import dark.Library.prefab.TileEntityMachine;
import dark.SteamPower.SteamPowerMain;

public class TileEntitytopGen extends TileEntityMachine {

    @Override
    public Object[] getSendData()
    {
        return null;
    }

    @Override
    public boolean needUpdate()
    {
        return false;
    }

    @Override
    public String getChannel()
    {
        return SteamPowerMain.channel;
    }

    @Override
    public int getSizeInventory()
    {
        return 0;
    }
	
}
