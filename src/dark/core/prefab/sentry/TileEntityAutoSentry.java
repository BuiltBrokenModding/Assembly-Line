package dark.core.prefab.sentry;


public class TileEntityAutoSentry extends TileEntitySentry
{
    @Override
    public SentryType getType()
    {
        return SentryType.AUTOMATED;
    }
}
