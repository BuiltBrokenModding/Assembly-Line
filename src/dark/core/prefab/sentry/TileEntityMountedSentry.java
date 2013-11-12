package dark.core.prefab.sentry;


public class TileEntityMountedSentry extends TileEntitySentry
{
    @Override
    public SentryType getType()
    {
        return SentryType.MOUNTED;
    }
}
