package dark.core.prefab.sentry;


public class TileEntityMountedSentry extends TileEntitySentry
{
    public TileEntityMountedSentry(float maxDamage)
    {
        super(maxDamage);
    }

    @Override
    public SentryType getType()
    {
        return SentryType.MOUNTED;
    }
}
