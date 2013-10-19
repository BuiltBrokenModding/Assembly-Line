package dark.api.access;

import java.util.HashMap;

public class TerminalPermissionRegistry
{
    public static HashMap<String, TerminalPermission> permission = new HashMap();

    static
    {
        registerPermission(new TerminalPermission("help", "general.help"));
        registerPermission(new TerminalPermission("access", "security.core.access"));
        registerPermission(new TerminalPermission("access.set", "security.core.access.set"));
    }

    public static void registerPermission(TerminalPermission p)
    {
        if (!permission.containsKey(p.getName()))
        {
            permission.put(p.getSaveName(), p);
        }
    }
}
