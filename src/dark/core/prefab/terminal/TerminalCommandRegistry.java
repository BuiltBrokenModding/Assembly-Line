package dark.core.prefab.terminal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import dark.api.ITerminal;
import dark.api.access.AccessGroup;
import dark.api.access.ISpecialAccess;
import dark.api.access.ITerminalCommand;

/** @author DarkGuardsman */
public class TerminalCommandRegistry
{
    public static final List<ITerminalCommand> COMMANDS = new ArrayList<ITerminalCommand>();
    public static final List<String> nodes = new ArrayList<String>();
    public static final HashMap<String, List<String>> groupDefaultNodes = new HashMap();
    public static final HashMap<String, String> groupDefaultExtends = new HashMap();

    static
    {
        List<String> list = new ArrayList<String>();
        //Owner group defaults
        list.add("group.owner");
        list.add("inv.disable");
        list.add("inv.enable");
        createDefaultGroup("owner", "admin", list);
        //Admin group defaults
        list.clear();
        list.add("group.admin");
        list.add("inv.edit");
        list.add("inv.lock");
        list.add("inv.unlock");
        list.add("inv.change");
        createDefaultGroup("admin", "user", list);
        //User group defaults
        list.clear();
        list.add("group.user");
        list.add("inv.open");
        list.add("inv.take");
        list.add("inv.give");
        createDefaultGroup("user", null, list);
    }

    /** Creates a default group for all machines to use. Only add a group if there is no option to
     * really manage the group's settings
     *
     * @param name - group name
     * @param prefabGroup - group this should extend. Make sure it exists.
     * @param nodes - all commands or custom nodes */
    public static void createDefaultGroup(String name, String prefabGroup, List<String> nodes)
    {
        if (name != null)
        {
            groupDefaultNodes.put(name, nodes);
            groupDefaultExtends.put(name, prefabGroup);
        }
    }

    /** Creates a default group for all machines to use. Only add a group if there is no option to
     * really manage the group's settings
     *
     * @param name - group name
     * @param prefabGroup - group this should extend. Make sure it exists.
     * @param nodes - all commands or custom nodes */
    public static void createDefaultGroup(String name, String prefabGroup, String... nodes)
    {
        List<String> nodeList = new ArrayList<String>();
        if (nodes != null)
        {
            for (String node : nodes)
            {
                nodeList.add(node);
            }
        }
        createDefaultGroup(name, prefabGroup, nodeList);
    }

    /** Builds a new default group list for a basic machine */
    public static List<AccessGroup> getNewGroupSet()
    {
        List<AccessGroup> groups = new ArrayList<AccessGroup>();
        for (Entry<String, List<String>> entry : groupDefaultNodes.entrySet())
        {
            AccessGroup group = new AccessGroup(entry.getKey());
            if (entry.getValue() != null)
            {
                for (String string : entry.getValue())
                {
                    group.addNode(string);
                }
            }
        }
        return groups;
    }

    /** Builds then loaded a new default group set into the terminal */
    public static void loadNewGroupSet(ISpecialAccess terminal)
    {
        if (terminal != null)
        {
            List<AccessGroup> groups = getNewGroupSet();
            for (AccessGroup group : groups)
            {
                terminal.addGroup(group);
            }
        }
    }

    /** @param prefix - what the command starts with for example /time
     * @param cmd - Cmd instance that will execute the command */
    public static void register(ITerminalCommand cmd, String group)
    {
        if (!COMMANDS.contains(cmd))
        {
            COMMANDS.add(cmd);
            if (group != null)
            {
                if (groupDefaultNodes.containsKey(group))
                {
                    List<String> stra = new ArrayList<String>();
                    stra.add(cmd.getCommandName());
                }
            }
        }
    }

    public static void register(String node)
    {
        if (!nodes.contains(node))
        {
            nodes.add(node);
        }
    }

    /** When a player uses a command in any CMD machine it pass threw here first
     *
     * @param terminal - The terminal, can be cast to TileEntity. */
    public static boolean onCommand(EntityPlayer player, ITerminal terminal, String cmd)
    {
        if (cmd != null && cmd != "")
        {
            String[] args = cmd.split(" ");

            if (args[0] != null)
            {
                for (ITerminalCommand command : COMMANDS)
                {
                    if (command.getCommandName().equalsIgnoreCase(args[0]))
                    {
                        if (command.canSupport(terminal) && command.getNode(args) != null)
                        {
                            if (!terminal.canUse(command.getNode(args), player))
                            {
                                terminal.addToConsole("Access Denied.");
                                return false;
                            }
                            else
                            {
                                if (command.called(player, terminal, args))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            terminal.addToConsole("Unknown Command.");
        }
        return false;
    }

}
