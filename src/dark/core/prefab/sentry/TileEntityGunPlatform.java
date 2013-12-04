package dark.core.prefab.sentry;

import dark.core.prefab.terminal.TileEntityTerminal;

public class TileEntityGunPlatform extends TileEntityTerminal
{
    public TileEntityGunPlatform()
    {
        super(0, 0);
    }

    public TileEntityGunPlatform(float wattsPerTick)
    {
        super(wattsPerTick);
    }

    public TileEntityGunPlatform(float wattsPerTick, float maxEnergy)
    {
        super(wattsPerTick, maxEnergy);
    }
}
