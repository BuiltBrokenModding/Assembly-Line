package dark.api.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;

/** @author DarkGuardsman */
public class GroupRegistry
{
    public static final List<ITerminalCommand> COMMANDS = new ArrayList<ITerminalCommand>();
    public static final List<String> nodes = new ArrayList<String>();
    public static final HashMap<String, List<String>> groupDefaultNodes = new HashMap();
    public static final HashMap<String, String> groupDefaultExtends = new HashMap();

    
    static
    {
        List<String> list = new ArrayList<String>();
        //Owner group defaults
        list.add(Nodes.GROUP_OWNER_NODE);
        list.add(Nodes.INV_DISABLE_NODE);
        list.add(Nodes.INV_ENABLE_NODE);
        createDefaultGroup("owner", "admin", list);
        //Admin group defaults
        list.clear();
        list.add(Nodes.GROUP_ADMIN_NODE);
        list.add(Nodes.INV_EDIT_NODE);
        list.add(Nodes.INV_LOCK_NODE);
        list.add(Nodes.INV_UNLOCK_NODE);
        list.add(Nodes.INV_CHANGE_NODE);
        createDefaultGroup("admin", "user", list);
        //User group defaults
        list.clear();
        list.add(Nodes.GROUP_USER_NODE);
        list.add(Nodes.INV_OPEN_NODE);
        list.add(Nodes.INV_TAKE_NODE);
        list.add(Nodes.INV_GIVE_NODE);
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
            groups.add(group);
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
