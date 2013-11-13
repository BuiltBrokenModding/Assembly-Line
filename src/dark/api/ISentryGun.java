package dark.api;

import net.minecraft.tileentity.TileEntity;
import dark.core.interfaces.IBlockActivated;
import dark.core.interfaces.IControlReceiver;
import dark.core.interfaces.IDamageableTile;

/** Applied to tile entities that are sentry guns
 * 
 * @author DarkGuardsman */
public interface ISentryGun extends IAimable, IDamageableTile, IControlReceiver, IBlockActivated
{
    /** Gets the type of sentry */
    public SentryType getType();

    /** Gets the tileEntity this sentry is attached too. Null if its self supporting */
    public TileEntity getPlatform();

    public static enum SentryType
    {
        /** Sentry guns that act like entities and are self moving */
        AUTOMATED(),
        /** Sentry guns that are manually moved by assisted input and are basically blocks */
        AIMED(),
        /** Sentry guns that are mounted by entities like players and are an extension of that entity */
        MOUNTED();
    }
}
