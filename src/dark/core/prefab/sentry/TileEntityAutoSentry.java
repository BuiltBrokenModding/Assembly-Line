package dark.core.prefab.sentry;

public class TileEntityAutoSentry extends TileEntitySentry
{
    public TileEntityAutoSentry(float maxDamage)
    {
        super(maxDamage);
    }

    @Override
    public SentryType getType()
    {
        return SentryType.AUTOMATED;
    }
}
