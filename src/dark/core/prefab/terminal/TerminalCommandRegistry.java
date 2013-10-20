package dark.core.prefab.terminal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import dark.api.ITerminal;
import dark.api.access.AccessGroup;
import dark.api.access.ITerminalCommand;

/** @author DarkGuardsman */
public class TerminalCommandRegistry
{
    public static final List<ITerminalCommand> COMMANDS = new ArrayList<ITerminalCommand>();
    public static final List<String> nodes = new ArrayList<String>();
    public static final HashMap<String, List<String>> groupDefaultNodes = new HashMap();

    static
    {
        List<String> list = new ArrayList<String>();
        //Owner group defaults
        list.add("group.owner");
        list.add("inv.disable");
        list.add("inv.enable");
        groupDefaultNodes.put("owner", list);
        //Admin group defaults
        list.clear();
        list.add("group.admin");
        list.add("inv.edit");
        list.add("inv.lock");
        list.add("inv.unlock");
        list.add("inv.change");
        groupDefaultNodes.put("admin", list);
        //User group defaults
        list.clear();
        list.add("group.user");
        list.add("inv.open");
        list.add("inv.take");
        list.add("inv.give");
        groupDefaultNodes.put("user", list);
    }

    /** Builds a new default group list for a basic machine */
    public static List<AccessGroup> createDefaultGroups()
    {
        List<AccessGroup> groups = new ArrayList<AccessGroup>();
        AccessGroup ownerPrefab = new AccessGroup("owner");
        AccessGroup adminPrefab = new AccessGroup("admin");
        AccessGroup userPrefab = new AccessGroup("user");
        ownerPrefab.setToExtend(adminPrefab);
        adminPrefab.setToExtend(userPrefab);
        List<String> groupNodes = groupDefaultNodes.get("owner");
        if (groupNodes != null)
        {
            for (String stra : groupNodes)
            {
                ownerPrefab.addNode(stra);
            }
        }
        groupNodes = groupDefaultNodes.get("admin");
        if (groupNodes != null)
        {
            for (String stra : groupNodes)
            {
                adminPrefab.addNode(stra);
            }
        }
        groupNodes = groupDefaultNodes.get("user");
        if (groupNodes != null)
        {
            for (String stra : groupNodes)
            {
                userPrefab.addNode(stra);
            }
        }

        return groups;
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
