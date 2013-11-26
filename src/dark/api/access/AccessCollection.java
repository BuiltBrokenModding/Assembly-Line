package dark.api.access;

/** Container of access groups that can be used by anything. Only the core machine should load/save
 * global access collections to prevent conflicts.
 * 
 * @author DarkGuardsman */
public class AccessCollection
{
    /** Was created from a save within a tile entity */
    public final boolean isLocal;
    ISpecialAccess machine;

    public AccessCollection(ISpecialAccess machine)
    {
        this.isLocal = true;
        this.machine = machine;
    }

    public AccessCollection()
    {
        this.isLocal = false;
    }
}
