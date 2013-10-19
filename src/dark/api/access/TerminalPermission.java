package dark.api.access;

/** Used too store information about a permission node with in a terminal like device. Such as ICBM
 * sentry guns, or GS locked chests.
 *
 * @author DarkGuardsman */
public class TerminalPermission
{
    protected String permissionName = "p";
    protected String saveName = "access.general.p";

    public TerminalPermission(String name)
    {
        this.permissionName = name;
        this.saveName = "access.general." + name;
    }

    public TerminalPermission(String name, String saveName)
    {
        this.permissionName = name;
        this.saveName = saveName;
    }

    public String getName()
    {
        return this.permissionName;
    }

    public String getSaveName()
    {
        return this.saveName;
    }
}
